package com.Aditya.DocBookApp.Repository;

import com.Aditya.DocBookApp.Entity.AppointmentEntity;
import com.Aditya.DocBookApp.Enum.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long>
{
    List<AppointmentEntity> findByPatientEmail(String email);

    List<AppointmentEntity> findByDoctorId(Long doctorId);

    List<AppointmentEntity> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);

    boolean existsByTimeSlotId(Long slotId);

    @Query("""
            SELECT a
            FROM AppointmentEntity a
            JOIN a.timeSlot t
            WHERE a.status = :status
              AND t.date = :date
              AND t.startTime BETWEEN :startTime AND :endTime
            """)
    List<AppointmentEntity> findUpcomingAppointments(
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
