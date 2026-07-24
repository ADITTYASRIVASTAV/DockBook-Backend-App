package com.Aditya.DocBookApp.Repository;

import com.Aditya.DocBookApp.Entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository  extends JpaRepository<PaymentEntity , Long>
{
    Optional<PaymentEntity> findByRazorpayOrderId(String razorpayOrderId);
    Optional<PaymentEntity> findByAppointmentId(Long appointmentId);
}
