package com.Aditya.DocBookApp.Service;

import com.Aditya.DocBookApp.DTO.DoctorRegisterRequest;
import com.Aditya.DocBookApp.DTO.DoctorResponse;
import com.Aditya.DocBookApp.DTO.DoctorSearchResponse;
import com.Aditya.DocBookApp.Enum.Specialization;

import java.util.List;

public interface DoctorService
{
    DoctorResponse registerDoctor(Long userId, DoctorRegisterRequest request);
    DoctorResponse registerDoctorByEmail(String email, DoctorRegisterRequest request);
    DoctorResponse getDoctorById(Long doctorId);
    DoctorResponse getDoctorByUserId(Long userId);
    DoctorResponse getDoctorByEmail(String email);
    DoctorResponse updateDoctor(Long userId, DoctorRegisterRequest request);
    DoctorResponse updateDoctorByEmail(String email, DoctorRegisterRequest request);
    void deleteDoctor(Long userId);
    void deleteDoctorByEmail(String email);
    DoctorSearchResponse getAllDoctors(int page, int size);
    DoctorSearchResponse searchDoctors(Specialization specialization, String city, String name, int page, int size);
    List<Specialization> getAllSpecializations();
    void updateDoctorAverageRating(Long doctorId);
}
