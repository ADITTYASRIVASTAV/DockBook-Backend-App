package com.Aditya.DocBookApp.ServiceImpl;

import com.Aditya.DocBookApp.DTO.SlotRequest;
import com.Aditya.DocBookApp.DTO.SlotResponse;
import com.Aditya.DocBookApp.Entity.Doctor;
import com.Aditya.DocBookApp.Entity.TimeSlotEntity;
import com.Aditya.DocBookApp.Entity.UserEntity;
import com.Aditya.DocBookApp.Exception.BadRequestException;
import com.Aditya.DocBookApp.Exception.ResourceNotFoundException;
import com.Aditya.DocBookApp.Repository.DoctorRepository;
import com.Aditya.DocBookApp.Repository.TimeSlotRepository;
import com.Aditya.DocBookApp.Repository.UserRepository;
import com.Aditya.DocBookApp.Service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Override
    public SlotResponse createSlot(SlotRequest request, String doctorEmail) {
        Doctor doctor = getDoctorByEmail(doctorEmail);
        if (request.getEndTime().isBefore(request.getStartTime()) ||
                request.getEndTime().equals(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        if (request.getDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot create slots for past dates");
        }

        if (timeSlotRepository.existsByDoctorIdAndDateAndStartTime(
                doctor.getId(), request.getDate(), request.getStartTime())) {
            throw new BadRequestException("A slot already exists for this date and start time");
        }

        TimeSlotEntity slot = new TimeSlotEntity();
        slot.setDoctor(doctor);
        slot.setDate(request.getDate());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setBooked(false);

        TimeSlotEntity savedSlot = timeSlotRepository.save(slot);
        return mapToResponse(savedSlot);
    }

    @Override
    public List<SlotResponse> getDoctorSlots(String doctorEmail) {
        Doctor doctor = getDoctorByEmail(doctorEmail);
        List<TimeSlotEntity> slots = timeSlotRepository.findByDoctorId(doctor.getId());
        return slots.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }

        List<TimeSlotEntity> availableSlots =
                timeSlotRepository.findByDoctorIdAndDateAndIsBookedFalse(doctorId, date);
        return availableSlots.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSlot(Long slotId, String doctorEmail) {
        Doctor doctor = getDoctorByEmail(doctorEmail);

        TimeSlotEntity slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + slotId));

        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new BadRequestException("You can only delete your own slots");
        }
        if (slot.isBooked()) {
            throw new BadRequestException("Cannot delete a slot that is already booked");
        }

        timeSlotRepository.delete(slot);
    }

    private Doctor getDoctorByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user: " + email));
    }

    private SlotResponse mapToResponse(TimeSlotEntity slot) {
        return SlotResponse.builder()
                .id(slot.getId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .isBooked(slot.isBooked())
                .doctorId(slot.getDoctor().getId())
                .doctorName(slot.getDoctor().getUser().getName())
                .build();
    }
}
