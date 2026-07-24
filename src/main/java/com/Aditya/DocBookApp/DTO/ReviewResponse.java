package com.Aditya.DocBookApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse
{
    private Long id;
    private String patientName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

}
