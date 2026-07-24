package com.Aditya.DocBookApp.Controller;

import com.Aditya.DocBookApp.DTO.PaymentRequest;
import com.Aditya.DocBookApp.DTO.PaymentResponse;
import com.Aditya.DocBookApp.DTO.PaymentVerifyRequest;
import com.Aditya.DocBookApp.Service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentResponse> createOrder(@Valid @RequestBody PaymentRequest request) {
        String patientEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        PaymentResponse response = paymentService.createOrder(request, patientEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        PaymentResponse response = paymentService.verifyPayment(request);
        return ResponseEntity.ok(response);
    }
}
