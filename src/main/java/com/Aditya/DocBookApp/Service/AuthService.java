package com.Aditya.DocBookApp.Service;

import com.Aditya.DocBookApp.DTO.AuthResponse;
import com.Aditya.DocBookApp.DTO.ForgotPasswordRequest;
import com.Aditya.DocBookApp.DTO.LoginRequest;
import com.Aditya.DocBookApp.DTO.OtpVerifyRequest;
import com.Aditya.DocBookApp.DTO.RegisterRequest;
import com.Aditya.DocBookApp.DTO.ResetPasswordRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse verifyOtp(OtpVerifyRequest request);
    AuthResponse login(LoginRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    AuthResponse refreshToken(String refreshToken);
    void resendOtp(String email);
}