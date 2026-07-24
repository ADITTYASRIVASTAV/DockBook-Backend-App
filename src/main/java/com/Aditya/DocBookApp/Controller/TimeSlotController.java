package com.Aditya.DocBookApp.Controller;

import com.Aditya.DocBookApp.DTO.SlotRequest;
import com.Aditya.DocBookApp.DTO.SlotResponse;
import com.Aditya.DocBookApp.Service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PostMapping("/api/doctor/slots")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<SlotResponse> createSlot(@Valid @RequestBody SlotRequest request)
    {
        String doctorEmail = getAuthenticatedEmail();
        SlotResponse response = timeSlotService.createSlot(request, doctorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/api/doctor/slots")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<SlotResponse>> getDoctorSlots()
    {
        String doctorEmail = getAuthenticatedEmail();
        List<SlotResponse> response = timeSlotService.getDoctorSlots(doctorEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/doctor/slots/{slotId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> deleteSlot(@PathVariable Long slotId)
    {
        String doctorEmail = getAuthenticatedEmail();
        timeSlotService.deleteSlot(slotId, doctorEmail);
        return ResponseEntity.ok("Slot deleted successfully");
    }

    @GetMapping("/api/doctors/{doctorId}/slots")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam LocalDate date)
    {
        List<SlotResponse> response = timeSlotService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(response);
    }

    private String getAuthenticatedEmail()
    {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
