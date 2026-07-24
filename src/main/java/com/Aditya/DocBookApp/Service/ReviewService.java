package com.Aditya.DocBookApp.Service;

import com.Aditya.DocBookApp.DTO.ReviewRequest;
import com.Aditya.DocBookApp.DTO.ReviewResponse;
import org.springframework.data.domain.Page;

public interface ReviewService
{
    ReviewResponse submitReview(ReviewRequest request, String patientEmail);
    Page<ReviewResponse> getDoctorReviews(Long doctorId, int page, int size);
}
