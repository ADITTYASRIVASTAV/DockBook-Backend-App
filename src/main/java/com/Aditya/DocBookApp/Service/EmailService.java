package com.Aditya.DocBookApp.Service;

import com.Aditya.DocBookApp.Entity.AppointmentEntity;
import com.Aditya.DocBookApp.Entity.PaymentEntity;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendBookingConfirmation(AppointmentEntity appointment);
    void sendBookingRejection(AppointmentEntity appointment);
    void sendBookingAccepted(AppointmentEntity appointment);
    void sendCancellationEmail(AppointmentEntity appointment);
    void sendPaymentReceipt(PaymentEntity payment);
    void sendAppointmentReminder(AppointmentEntity appointment);
    void sendPasswordResetOtp(String toEmail, String otp);
}
