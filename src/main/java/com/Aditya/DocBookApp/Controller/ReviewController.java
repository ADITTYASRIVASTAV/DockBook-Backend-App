package com.Aditya.DocBookApp.Controller;

import com.Aditya.DocBookApp.DTO.ReviewRequest;
import com.Aditya.DocBookApp.DTO.ReviewResponse;
import com.Aditya.DocBookApp.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ReviewResponse submitReview(@Valid @RequestBody ReviewRequest request) {
        String patientEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return reviewService.submitReview(request, patientEmail);
    }


    @GetMapping("/doctors/{doctorId}/reviews")
    public Page<ReviewResponse> getDoctorReviews(@PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        return reviewService.getDoctorReviews(doctorId, page, size);
    }

}
