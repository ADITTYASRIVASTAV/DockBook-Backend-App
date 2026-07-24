package com.Aditya.DocBookApp.Entity;

import com.Aditya.DocBookApp.Enum.Specialization;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Doctors")
@Builder
public class Doctor
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false , unique = true)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Specialization specialization;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String qualification;

    @Column(nullable = false)
    private Integer experience;

    @Column(nullable = false)
    private Double fee;

    private String hospital;

    @Column(name = "avg_rating", nullable = false)
    private Double avgRating = 0.0;

    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;
}
