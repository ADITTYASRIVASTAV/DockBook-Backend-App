package com.Aditya.DocBookApp.Controller;

import com.Aditya.DocBookApp.DTO.ChangePasswordRequest;
import com.Aditya.DocBookApp.DTO.UpdateProfileRequest;
import com.Aditya.DocBookApp.Entity.UserEntity;
import com.Aditya.DocBookApp.Exception.BadRequestException;
import com.Aditya.DocBookApp.Exception.ResourceNotFoundException;
import com.Aditya.DocBookApp.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/profile-image")
    public ResponseEntity<Map<String, String>> updateProfileImage(@RequestBody Map<String, String> request) {
        String email = getAuthenticatedEmail();
        UserEntity user = getUserByEmail(email);

        String imageUrl = request.get("profileImage"); // This will now be a Base64 string
        user.setProfileImage(imageUrl);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile image updated successfully");
        response.put("profileImage", imageUrl);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody UpdateProfileRequest request) {
        String email = getAuthenticatedEmail();
        UserEntity user = getUserByEmail(email);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        response.put("name", user.getName());
        response.put("phoneNumber", user.getPhoneNumber());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String email = getAuthenticatedEmail();
        UserEntity user = getUserByEmail(email);
        if (user.getPassword() == null)
        {
            throw new BadRequestException("Cannot change password for " + user.getProvider() + " accounts. Please use " + user.getProvider() + " to manage your password.");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        String email = getAuthenticatedEmail();
        UserEntity user = getUserByEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("role", user.getRole().name());
        response.put("profileImage", user.getProfileImage());
        response.put("provider", user.getProvider());
        response.put("createdAt", user.getCreatedAt());
        return ResponseEntity.ok(response);
    }

    private String getAuthenticatedEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
