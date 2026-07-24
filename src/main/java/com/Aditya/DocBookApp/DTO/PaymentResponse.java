package com.Aditya.DocBookApp.DTO;

import com.Aditya.DocBookApp.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse
{
    private Long id;
    private Long appointmentId;
    private Double amount;
    private String razorpayOrderId;
    private PaymentStatus status;
    private String message;
}
