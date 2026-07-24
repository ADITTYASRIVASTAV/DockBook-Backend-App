package com.Aditya.DocBookApp.Service;

import com.Aditya.DocBookApp.DTO.AppointmentResponse;
import com.Aditya.DocBookApp.DTO.BookingRequest;

import java.util.List;

public interface AppointmentService
{
    AppointmentResponse bookAppointment(BookingRequest request, String patientEmail);

    List<AppointmentResponse> getPatientAppointments(String patientEmail);

    List<AppointmentResponse> getDoctorAppointments(String doctorEmail);

    AppointmentResponse acceptAppointment(Long appointmentId, String doctorEmail);

    AppointmentResponse rejectAppointment(Long appointmentId, String doctorEmail);

    AppointmentResponse completeAppointment(Long appointmentId, String doctorEmail);

    AppointmentResponse cancelAppointment(Long appointmentId, String patientEmail);
}
