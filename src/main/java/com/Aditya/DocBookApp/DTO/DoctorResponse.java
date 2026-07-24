package com.Aditya.DocBookApp.DTO;

import com.Aditya.DocBookApp.Enum.Specialization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorResponse {
    private Long id;
    private String name;
    private String email;
    private String profileImage;
    private Specialization specialization;
    private String city;
    private String qualification;
    private Integer experience;
    private Double fee;
    private String hospital;
    private Double avgRating;
    private Integer totalReviews;
}
