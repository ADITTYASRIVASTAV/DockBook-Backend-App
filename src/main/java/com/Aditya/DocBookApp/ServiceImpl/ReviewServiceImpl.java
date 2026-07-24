package com.Aditya.DocBookApp.ServiceImpl;

import com.Aditya.DocBookApp.DTO.ReviewRequest;
import com.Aditya.DocBookApp.DTO.ReviewResponse;
import com.Aditya.DocBookApp.Entity.AppointmentEntity;
import com.Aditya.DocBookApp.Entity.Doctor;
import com.Aditya.DocBookApp.Entity.ReviewEntity;
import com.Aditya.DocBookApp.Entity.UserEntity;
import com.Aditya.DocBookApp.Enum.AppointmentStatus;
import com.Aditya.DocBookApp.Exception.BadRequestException;
import com.Aditya.DocBookApp.Exception.ResourceNotFoundException;
import com.Aditya.DocBookApp.Repository.AppointmentRepository;
import com.Aditya.DocBookApp.Repository.DoctorRepository;
import com.Aditya.DocBookApp.Repository.ReviewRepository;
import com.Aditya.DocBookApp.Repository.UserRepository;
import com.Aditya.DocBookApp.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService
{
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public ReviewResponse submitReview(ReviewRequest request, String patientEmail)
    {
        UserEntity patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        AppointmentEntity appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patient.getId()))
        {
            throw new BadRequestException("You can only review your own appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED)
        {
            throw new BadRequestException("You can only review completed appointments");
        }

        if (reviewRepository.existsByAppointmentId(request.getAppointmentId()))
        {
            throw new BadRequestException("Review already exists for this appointment");
        }

        if (request.getRating() < 1 || request.getRating() > 5)
        {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        ReviewEntity review = new ReviewEntity();
        review.setAppointment(appointment);
        review.setPatient(patient);
        review.setDoctor(appointment.getDoctor());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        ReviewEntity savedReview = reviewRepository.save(review);

        Double avg = reviewRepository.findAverageRatingByDoctorId(appointment.getDoctor().getId());

        Doctor doctor = appointment.getDoctor();
        doctor.setAvgRating(avg != null ? avg : 0.0);
        doctor.setTotalReviews(doctor.getTotalReviews() + 1);
        doctorRepository.save(doctor);

        return ReviewResponse.builder()
                .id(savedReview.getId())
                .patientName(patient.getName())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    @Override
    public Page<ReviewResponse> getDoctorReviews(Long doctorId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ReviewEntity> reviews = reviewRepository.findByDoctorId(doctorId, pageable);

        return reviews.map(review -> ReviewResponse.builder()
                .id(review.getId())
                .patientName(review.getPatient().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build()
        );
    }
}