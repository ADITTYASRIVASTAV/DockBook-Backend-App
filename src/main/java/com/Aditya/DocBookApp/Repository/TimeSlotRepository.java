package com.Aditya.DocBookApp.Repository;

import com.Aditya.DocBookApp.Entity.TimeSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlotEntity, Long>
{
    List<TimeSlotEntity> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    List<TimeSlotEntity> findByDoctorIdAndDateAndIsBookedFalse(Long doctorId, LocalDate date);

    List<TimeSlotEntity> findByDoctorId(Long doctorId);

    boolean existsByDoctorIdAndDateAndStartTime(Long doctorId, LocalDate date, LocalTime startTime);
}
