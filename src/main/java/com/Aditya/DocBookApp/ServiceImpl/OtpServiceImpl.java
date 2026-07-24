package com.Aditya.DocBookApp.ServiceImpl;

import com.Aditya.DocBookApp.Service.OtpService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpServiceImpl implements OtpService {
    private final SecureRandom random = new SecureRandom();
    @Override
    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000); // always 6 digits
        return String.valueOf(otp);
    }
    @Override
    public boolean validateOtp(String inputOtp, String storedOtp) {
        if (inputOtp == null || storedOtp == null) {
            return false;
        }
        return inputOtp.equals(storedOtp);
    }
}
