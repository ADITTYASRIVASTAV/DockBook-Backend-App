package com.Aditya.DocBookApp.ServiceImpl;

import com.Aditya.DocBookApp.DTO.DoctorRegisterRequest;
import com.Aditya.DocBookApp.DTO.DoctorResponse;
import com.Aditya.DocBookApp.DTO.DoctorSearchResponse;
import com.Aditya.DocBookApp.Entity.Doctor;
import com.Aditya.DocBookApp.Entity.UserEntity;
import com.Aditya.DocBookApp.Enum.Specialization;
import com.Aditya.DocBookApp.Exception.DuplicateResourceException;
import com.Aditya.DocBookApp.Exception.ResourceNotFoundException;
import com.Aditya.DocBookApp.Repository.DoctorRepository;
import com.Aditya.DocBookApp.Repository.ReviewRepository;
import com.Aditya.DocBookApp.Repository.UserRepository;
import com.Aditya.DocBookApp.Service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;


    @Override
    public DoctorResponse registerDoctor(Long userId, DoctorRegisterRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (doctorRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException("Doctor profile already exists for this user");
        }
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialization(request.getSpecialization());
        doctor.setCity(request.getCity());
        doctor.setQualification(request.getQualification());
        doctor.setExperience(request.getExperience());
        doctor.setFee(request.getFee());
        doctor.setHospital(request.getHospital());
        Doctor savedDoctor = doctorRepository.save(doctor);
        return mapToResponse(savedDoctor);
    }

    @Override
    public DoctorResponse getDoctorById(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        return mapToResponse(doctor);
    }

    @Override
    public DoctorResponse getDoctorByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user id: " + userId));
        return mapToResponse(doctor);
    }

    @Override
    public DoctorResponse updateDoctor(Long userId, DoctorRegisterRequest request) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user id: " + userId));
        doctor.setSpecialization(request.getSpecialization());
        doctor.setCity(request.getCity());
        doctor.setQualification(request.getQualification());
        doctor.setExperience(request.getExperience());
        doctor.setFee(request.getFee());
        doctor.setHospital(request.getHospital());
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return mapToResponse(updatedDoctor);
    }

    @Override
    public void deleteDoctor(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user id: " + userId));
        doctorRepository.delete(doctor);
    }

    @Override
    public DoctorResponse registerDoctorByEmail(String email, DoctorRegisterRequest request) {
        Long userId = getUserIdByEmail(email);
        return registerDoctor(userId, request);
    }

    @Override
    public DoctorResponse getDoctorByEmail(String email) {
        Long userId = getUserIdByEmail(email);
        return getDoctorByUserId(userId);
    }

    @Override
    public DoctorResponse updateDoctorByEmail(String email, DoctorRegisterRequest request) {
        Long userId = getUserIdByEmail(email);
        return updateDoctor(userId, request);
    }

    @Override
    public void deleteDoctorByEmail(String email) {
        Long userId = getUserIdByEmail(email);
        deleteDoctor(userId);
    }

    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email))
                .getId();
    }

    @Override
    public DoctorSearchResponse getAllDoctors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Doctor> doctorPage = doctorRepository.findAll(pageable);
        return mapToSearchResponse(doctorPage);
    }

    @Override
    public DoctorSearchResponse searchDoctors(Specialization specialization, String city, String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Doctor> doctorPage = doctorRepository.searchDoctors(specialization, city, name, pageable);
        return mapToSearchResponse(doctorPage);
    }

    @Override
    public List<Specialization> getAllSpecializations() {
        return Arrays.asList(Specialization.values());
    }


    private DoctorResponse mapToResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .email(doctor.getUser().getEmail())
                .profileImage(doctor.getUser().getProfileImage())
                .specialization(doctor.getSpecialization())
                .city(doctor.getCity())
                .qualification(doctor.getQualification())
                .experience(doctor.getExperience())
                .fee(doctor.getFee())
                .hospital(doctor.getHospital())
                .avgRating(doctor.getAvgRating())
                .totalReviews(doctor.getTotalReviews())
                .build();
    }

    private DoctorSearchResponse mapToSearchResponse(Page<Doctor> doctorPage) {
        List<DoctorResponse> doctors = doctorPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return DoctorSearchResponse.builder()
                .content(doctors)
                .pageNumber(doctorPage.getNumber())
                .pageSize(doctorPage.getSize())
                .totalElements(doctorPage.getTotalElements())
                .totalPages(doctorPage.getTotalPages())
                .lastPage(doctorPage.isLast())
                .build();
    }

    @Override
    public void updateDoctorAverageRating(Long doctorId) {

        Double averageRating = reviewRepository.findAverageRatingByDoctorId(doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        if (averageRating == null)
        {
            doctor.setAvgRating(0.0);
        }
        else
        {
            doctor.setAvgRating(averageRating);
        }
        doctorRepository.save(doctor);
    }
}
