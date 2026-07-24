package com.Aditya.DocBookApp.Controller;

import com.Aditya.DocBookApp.DTO.DoctorRegisterRequest;
import com.Aditya.DocBookApp.DTO.DoctorResponse;
import com.Aditya.DocBookApp.DTO.DoctorSearchResponse;
import com.Aditya.DocBookApp.Enum.Specialization;
import com.Aditya.DocBookApp.Service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorResponse> registerDoctor(
            Authentication authentication,
            @Valid @RequestBody DoctorRegisterRequest request)
    {
        String email = authentication.getName();
        DoctorResponse response = doctorService.registerDoctorByEmail(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorResponse> getOwnProfile(Authentication authentication)
    {
        String email = authentication.getName();
        DoctorResponse response = doctorService.getDoctorByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorResponse> updateDoctor(
            Authentication authentication,
            @Valid @RequestBody DoctorRegisterRequest request)
    {
        String email = authentication.getName();
        DoctorResponse response = doctorService.updateDoctorByEmail(email, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> deleteDoctor(Authentication authentication)
    {
        String email = authentication.getName();
        doctorService.deleteDoctorByEmail(email);
        return ResponseEntity.ok("Doctor profile deleted successfully");
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long doctorId)
    {
        DoctorResponse response = doctorService.getDoctorById(doctorId);
        return ResponseEntity.ok(response);
    }

    /* Enforces 6 doctors per page by default for the general list */
    @GetMapping("/AllDoctors")
    public ResponseEntity<DoctorSearchResponse> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size)
    {
        DoctorSearchResponse response = doctorService.getAllDoctors(page, size);
        return ResponseEntity.ok(response);
    }

    /* Enforces 6 doctors per page by default for search results */
    @GetMapping("/search")
    public ResponseEntity<DoctorSearchResponse> searchDoctors(
            @RequestParam(required = false) Specialization specialization,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size)
    {
        DoctorSearchResponse response = doctorService.searchDoctors(specialization, city, name, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<Specialization>> getAllSpecializations()
    {
        List<Specialization> specializations = doctorService.getAllSpecializations();
        return ResponseEntity.ok(specializations);
    }
}