package com.Aditya.DocBookApp.Controller;
import com.Aditya.DocBookApp.DTO.AuthResponse;
import com.Aditya.DocBookApp.DTO.ForgotPasswordRequest;
import com.Aditya.DocBookApp.DTO.LoginRequest;
import com.Aditya.DocBookApp.DTO.OtpVerifyRequest;
import com.Aditya.DocBookApp.DTO.RegisterRequest;
import com.Aditya.DocBookApp.DTO.ResetPasswordRequest;
import com.Aditya.DocBookApp.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request)
    {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request)
    {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, String>> resendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.resendOtp(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP resent to your email address");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent to your email address");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<Map<String, String>> oauthSuccess(
            @RequestParam String token,
            @RequestParam(required = false) String refreshToken) {
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("message", "OAuth2 login successful");
        if (refreshToken != null) {
            response.put("refreshToken", refreshToken);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/google")
    public void googleLogin(@RequestParam(defaultValue = "PATIENT") String role,
                            HttpServletResponse response) throws IOException
    {
        Cookie cookie = new Cookie("oauth2_role", role);
        cookie.setPath("/");
        cookie.setMaxAge(300);
        response.addCookie(cookie);
        response.sendRedirect("/oauth2/authorization/google");
    }
}
