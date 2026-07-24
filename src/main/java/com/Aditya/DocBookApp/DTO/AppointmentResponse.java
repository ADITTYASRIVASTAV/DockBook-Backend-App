package com.Aditya.DocBookApp.DTO;

import com.Aditya.DocBookApp.Enum.AppointmentStatus;
import com.Aditya.DocBookApp.Enum.PaymentStatus;
import com.Aditya.DocBookApp.Enum.Specialization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse
{
    private Long id;
    private String patientName;
    private String patientEmail;
    private String doctorName;
    private Specialization doctorSpecialization;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String problemDescription;
    private Double fee;
    private LocalDateTime createdAt;
    private PaymentStatus paymentStatus;
}
