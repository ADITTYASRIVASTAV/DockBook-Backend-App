package com.Aditya.DocBookApp.Service;

import com.Aditya.DocBookApp.DTO.PaymentRequest;
import com.Aditya.DocBookApp.DTO.PaymentResponse;
import com.Aditya.DocBookApp.DTO.PaymentVerifyRequest;

public interface PaymentService
{
    PaymentResponse createOrder(PaymentRequest request, String patientEmail);
    PaymentResponse verifyPayment(PaymentVerifyRequest request);
}
