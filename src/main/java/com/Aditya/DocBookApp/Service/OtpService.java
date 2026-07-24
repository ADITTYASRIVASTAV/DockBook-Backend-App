package com.Aditya.DocBookApp.Service;

public interface OtpService {
    String generateOtp();
    boolean validateOtp(String inputOtp, String storedOtp);
}
