package com.Aditya.DocBookApp.Repository;

import com.Aditya.DocBookApp.Entity.Doctor;
import com.Aditya.DocBookApp.Enum.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>
{
    Optional<Doctor> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Page<Doctor> findBySpecialization(Specialization specialization, Pageable pageable);

    Page<Doctor> findBySpecializationAndCityIgnoreCase(Specialization specialization, String city, Pageable pageable);

    Page<Doctor> findByUser_NameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE " +
            "(:specialization IS NULL OR d.specialization = :specialization) AND " +
            "(:city IS NULL OR LOWER(d.city) = LOWER(:city)) AND " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Doctor> searchDoctors(
            @Param("specialization") Specialization specialization,
            @Param("city") String city,
            @Param("name") String name,
            Pageable pageable
    );
}
