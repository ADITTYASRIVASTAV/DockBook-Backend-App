package com.Aditya.DocBookApp.ServiceImpl;

import com.Aditya.DocBookApp.DTO.*;
import com.Aditya.DocBookApp.Entity.UserEntity;
import com.Aditya.DocBookApp.Exception.BadRequestException;
import com.Aditya.DocBookApp.Exception.DuplicateResourceException;
import com.Aditya.DocBookApp.Exception.ResourceNotFoundException;
import com.Aditya.DocBookApp.Repository.UserRepository;
import com.Aditya.DocBookApp.Security.JwtUtils;
import com.Aditya.DocBookApp.Service.AuthService;
import com.Aditya.DocBookApp.Service.EmailService;
import com.Aditya.DocBookApp.Service.OtpService;
import com.Aditya.DocBookApp.Utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final OtpService otpService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setEmailVerified(false);
        user.setProvider("LOCAL");

        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRY_MINUTES));

        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);

        return AuthResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .message("OTP sent to your email. Please verify to complete registration.")
                .build();
    }

    @Override
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }

        if (user.getOtp() == null || !otpService.validateOtp(request.getOtp(), user.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return generateAuthResponse(user, "Email verified successfully! You are now logged in.");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isEmailVerified()) {
            throw new BadRequestException("Email not verified. Please verify your email first.");
        }

        if (user.getPassword() == null) {
            throw new BadRequestException("This account uses " + user.getProvider() + " login. Please login via " + user.getProvider() + ".");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        return generateAuthResponse(user, "Login successful");
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        String otp = otpService.generateOtp();

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRY_MINUTES));
        userRepository.save(user);
        emailService.sendPasswordResetOtp(user.getEmail(), otp);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        if (user.getOtp() == null || !otpService.validateOtp(request.getOtp(), user.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }
        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token. Please login again.");
        }


        String email = jwtUtils.getEmailFromToken(refreshToken);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String newAccessToken = jwtUtils.generateAccessToken(email, user.getRole().name());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .profileImage(user.getProfileImage())
                .message("Token refreshed successfully")
                .build();
    }

    @Override
    public void resendOtp(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }
        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRY_MINUTES));
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    private AuthResponse generateAuthResponse(UserEntity user, String message) {
        String accessToken = jwtUtils.generateAccessToken(
                user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .profileImage(user.getProfileImage())
                .message(message)
                .build();
    }
}