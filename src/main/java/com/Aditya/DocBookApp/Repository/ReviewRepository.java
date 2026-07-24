package com.Aditya.DocBookApp.Repository;

import com.Aditya.DocBookApp.Entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository  extends JpaRepository<ReviewEntity , Long>
{

    Page<ReviewEntity> findByDoctorId(Long doctorId, Pageable pageable);
    boolean existsByAppointmentId(Long appointmentId);
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.doctor.id = :doctorId")
    Double findAverageRatingByDoctorId(@Param("doctorId") Long doctorId);
}
