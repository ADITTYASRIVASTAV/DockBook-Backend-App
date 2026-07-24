package com.Aditya.DocBookApp.DTO;

import com.Aditya.DocBookApp.Enum.Specialization;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegisterRequest
{
    @NotNull(message = "Specialization is required")
    private Specialization specialization;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experience;

    @NotNull(message = "Consultation fee is required")
    @Positive(message = "Fee must be greater than zero")
    private Double fee;

    private String hospital;
}
