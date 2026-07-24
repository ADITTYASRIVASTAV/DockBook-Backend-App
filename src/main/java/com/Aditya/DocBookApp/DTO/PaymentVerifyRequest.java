package com.Aditya.DocBookApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyRequest
{
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

}
