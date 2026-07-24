package com.Aditya.DocBookApp.ServiceImpl;

import com.Aditya.DocBookApp.DTO.AppointmentResponse;
import com.Aditya.DocBookApp.DTO.BookingRequest;
import com.Aditya.DocBookApp.Entity.AppointmentEntity;
import com.Aditya.DocBookApp.Entity.Doctor;
import com.Aditya.DocBookApp.Entity.TimeSlotEntity;
import com.Aditya.DocBookApp.Entity.UserEntity;
import com.Aditya.DocBookApp.Enum.AppointmentStatus;
import com.Aditya.DocBookApp.Exception.BadRequestException;
import com.Aditya.DocBookApp.Exception.ResourceNotFoundException;
import com.Aditya.DocBookApp.Repository.AppointmentRepository;
import com.Aditya.DocBookApp.Repository.DoctorRepository;
import com.Aditya.DocBookApp.Repository.PaymentRepository;
import com.Aditya.DocBookApp.Repository.TimeSlotRepository;
import com.Aditya.DocBookApp.Repository.UserRepository;
import com.Aditya.DocBookApp.Service.AppointmentService;
import com.Aditya.DocBookApp.Service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(BookingRequest request, String patientEmail) {
        UserEntity patient = getUserByEmail(patientEmail);

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));
        TimeSlotEntity slot = timeSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + request.getSlotId()));

        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new BadRequestException("This slot does not belong to the specified doctor");
        }

        if (slot.isBooked()) {
            throw new BadRequestException("This time slot is already booked");
        }

        if (appointmentRepository.existsByTimeSlotId(slot.getId())) {
            throw new BadRequestException("An appointment already exists for this time slot");
        }

        slot.setBooked(true);
        timeSlotRepository.save(slot);

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setTimeSlot(slot);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setProblemDescription(request.getProblemDescription());
        AppointmentEntity savedAppointment = appointmentRepository.save(appointment);

        // Pre-touch lazy proxies inside active transaction before async email execution
        if (savedAppointment.getPatient() != null) savedAppointment.getPatient().getName();
        if (savedAppointment.getDoctor() != null && savedAppointment.getDoctor().getUser() != null) savedAppointment.getDoctor().getUser().getName();
        if (savedAppointment.getTimeSlot() != null) savedAppointment.getTimeSlot().getDate();

        try {
            emailService.sendBookingConfirmation(savedAppointment);
        } catch (Exception e) {
            log.error("Error sending booking confirmation email: {}", e.getMessage());
        }
        return mapToResponse(savedAppointment);
    }

    @Override
    public List<AppointmentResponse> getPatientAppointments(String patientEmail) {
        List<AppointmentEntity> appointments = appointmentRepository.findByPatientEmail(patientEmail);
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, String patientEmail) {
        AppointmentEntity appointment = getAppointmentById(appointmentId);
        if (!appointment.getPatient().getEmail().equals(patientEmail)) {
            throw new BadRequestException("You can only cancel your own appointments");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING &&
                appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Cannot cancel appointment with status: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        TimeSlotEntity slot = appointment.getTimeSlot();
        slot.setBooked(false);
        timeSlotRepository.save(slot);

        AppointmentEntity updated = appointmentRepository.save(appointment);

        // Touch lazy proxies inside active transaction before async email execution
        if (updated.getPatient() != null) updated.getPatient().getName();
        if (updated.getDoctor() != null && updated.getDoctor().getUser() != null) updated.getDoctor().getUser().getName();
        if (updated.getTimeSlot() != null) updated.getTimeSlot().getDate();

        try {
            emailService.sendCancellationEmail(updated);
        } catch (Exception e) {
            log.error("Error sending cancellation email: {}", e.getMessage());
        }
        return mapToResponse(updated);
    }

    @Override
    public List<AppointmentResponse> getDoctorAppointments(String doctorEmail) {
        Doctor doctor = getDoctorByEmail(doctorEmail);
        List<AppointmentEntity> appointments = appointmentRepository.findByDoctorId(doctor.getId());
        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentResponse acceptAppointment(Long appointmentId, String doctorEmail) {
        AppointmentEntity appointment = getAppointmentById(appointmentId);
        verifyDoctorOwnership(appointment, doctorEmail);

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BadRequestException("Can only accept appointments with PENDING status. Current status: " + appointment.getStatus());
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        AppointmentEntity updated = appointmentRepository.save(appointment);

        // Touch lazy proxies inside active transaction before async email execution
        if (updated.getPatient() != null) updated.getPatient().getName();
        if (updated.getDoctor() != null && updated.getDoctor().getUser() != null) updated.getDoctor().getUser().getName();
        if (updated.getTimeSlot() != null) updated.getTimeSlot().getDate();

        try {
            emailService.sendBookingAccepted(updated);
        } catch (Exception e) {
            log.error("Error sending booking accepted email: {}", e.getMessage());
        }
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public AppointmentResponse rejectAppointment(Long appointmentId, String doctorEmail) {
        AppointmentEntity appointment = getAppointmentById(appointmentId);
        verifyDoctorOwnership(appointment, doctorEmail);
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BadRequestException("Can only reject appointments with PENDING status. Current status: "
                    + appointment.getStatus());
        }
        appointment.setStatus(AppointmentStatus.REJECTED);
        TimeSlotEntity slot = appointment.getTimeSlot();
        slot.setBooked(false);
        timeSlotRepository.save(slot);
        AppointmentEntity updated = appointmentRepository.save(appointment);

        // Touch lazy proxies inside active transaction before async email execution
        if (updated.getPatient() != null) updated.getPatient().getName();
        if (updated.getDoctor() != null && updated.getDoctor().getUser() != null) updated.getDoctor().getUser().getName();
        if (updated.getTimeSlot() != null) updated.getTimeSlot().getDate();

        try {
            emailService.sendBookingRejection(updated);
        } catch (Exception e) {
            log.error("Error sending booking rejection email: {}", e.getMessage());
        }
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public AppointmentResponse completeAppointment(Long appointmentId, String doctorEmail) {
        AppointmentEntity appointment = getAppointmentById(appointmentId);
        verifyDoctorOwnership(appointment, doctorEmail);
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Can only complete appointments with CONFIRMED status. Current status: "
                    + appointment.getStatus());
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        AppointmentEntity updated = appointmentRepository.saveAndFlush(appointment);
        return mapToResponse(updated);
    }

    private AppointmentEntity getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
    }

    private UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Doctor getDoctorByEmail(String email) {
        UserEntity user = getUserByEmail(email);
        return doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user: " + email));
    }

    private void verifyDoctorOwnership(AppointmentEntity appointment, String doctorEmail) {
        Doctor doctor = getDoctorByEmail(doctorEmail);
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new BadRequestException("You can only manage your own appointments");
        }
    }

    private AppointmentResponse mapToResponse(AppointmentEntity appointment) {
        // Extract entity properties first to initialize lazy proxies safely
        Long id = appointment.getId();
        String patientName = appointment.getPatient() != null ? appointment.getPatient().getName() : "";
        String patientEmail = appointment.getPatient() != null ? appointment.getPatient().getEmail() : "";
        String doctorName = (appointment.getDoctor() != null && appointment.getDoctor().getUser() != null) ? appointment.getDoctor().getUser().getName() : "";
        com.Aditya.DocBookApp.Enum.Specialization doctorSpecialization = appointment.getDoctor() != null ? appointment.getDoctor().getSpecialization() : null;
        Double fee = appointment.getDoctor() != null ? appointment.getDoctor().getFee() : 0.0;
        
        java.time.LocalDate date = appointment.getTimeSlot() != null ? appointment.getTimeSlot().getDate() : null;
        java.time.LocalTime startTime = appointment.getTimeSlot() != null ? appointment.getTimeSlot().getStartTime() : null;
        java.time.LocalTime endTime = appointment.getTimeSlot() != null ? appointment.getTimeSlot().getEndTime() : null;

        // Now query payment status safely after proxies are resolved
        com.Aditya.DocBookApp.Enum.PaymentStatus pStatus = null;
        if (id != null) {
            try {
                pStatus = paymentRepository
                        .findByAppointmentId(id)
                        .map(payment -> payment.getStatus())
                        .orElse(null);
            } catch (Throwable t) {
                log.warn("Could not fetch payment status for appointment {}: {}", id, t.getMessage());
            }
        }

        return AppointmentResponse.builder()
                .id(id)
                .patientName(patientName)
                .patientEmail(patientEmail)
                .doctorName(doctorName)
                .doctorSpecialization(doctorSpecialization)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .status(appointment.getStatus())
                .problemDescription(appointment.getProblemDescription())
                .fee(fee)
                .createdAt(appointment.getCreatedAt())
                .paymentStatus(pStatus)
                .build();
    }
}
