package com.Aditya.DocBookApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String refreshToken;
    private String accessToken;
    private String email;
    private String name;
    private String role;
    private String message;
    private String profileImage;
}
