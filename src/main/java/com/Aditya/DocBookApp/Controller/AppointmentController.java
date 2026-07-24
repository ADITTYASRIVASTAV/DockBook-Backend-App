package com.Aditya.DocBookApp.Controller;

import com.Aditya.DocBookApp.DTO.AppointmentResponse;
import com.Aditya.DocBookApp.DTO.BookingRequest;
import com.Aditya.DocBookApp.Service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/api/appointments")
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody BookingRequest request)
    {
        String patientEmail = getAuthenticatedEmail();
        AppointmentResponse response = appointmentService.bookAppointment(request, patientEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/appointments/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments()
    {
        String patientEmail = getAuthenticatedEmail();
        List<AppointmentResponse> response = appointmentService.getPatientAppointments(patientEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/appointments/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id)
    {
        String patientEmail = getAuthenticatedEmail();
        AppointmentResponse response = appointmentService.cancelAppointment(id, patientEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/doctor/appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments()
    {
        String doctorEmail = getAuthenticatedEmail();
        List<AppointmentResponse> response = appointmentService.getDoctorAppointments(doctorEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/doctor/appointments/{id}/accept")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponse> acceptAppointment(@PathVariable Long id)
    {
        String doctorEmail = getAuthenticatedEmail();
        AppointmentResponse response = appointmentService.acceptAppointment(id, doctorEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/doctor/appointments/{id}/reject")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponse> rejectAppointment(@PathVariable Long id)
    {
        String doctorEmail = getAuthenticatedEmail();
        AppointmentResponse response = appointmentService.rejectAppointment(id, doctorEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/doctor/appointments/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponse> completeAppointment(@PathVariable Long id)
    {
        String doctorEmail = getAuthenticatedEmail();
        AppointmentResponse response = appointmentService.completeAppointment(id, doctorEmail);
        return ResponseEntity.ok(response);
    }
    private String getAuthenticatedEmail()
    {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
