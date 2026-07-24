package com.Aditya.DocBookApp.ServiceImpl;

import com.Aditya.DocBookApp.Entity.AppointmentEntity;
import com.Aditya.DocBookApp.Entity.PaymentEntity;
import com.Aditya.DocBookApp.Service.EmailService;
import com.Aditya.DocBookApp.Utils.EmailTemplateUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("DocBook - OTP Verification");
            helper.setText(EmailTemplateUtil.buildOtpEmail(otp), true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendBookingConfirmation(AppointmentEntity appointment) {
        try {
            String to = appointment.getPatient().getEmail();
            String patientName = appointment.getPatient().getName();
            String doctorName = appointment.getDoctor().getUser().getName();
            String specialization = appointment.getDoctor().getSpecialization() != null ? appointment.getDoctor().getSpecialization().name() : "GENERAL";
            String hospital = appointment.getDoctor().getHospital() != null ? appointment.getDoctor().getHospital() : "DocBook Clinic";
            String date = appointment.getTimeSlot().getDate().toString();
            String time = appointment.getTimeSlot().getStartTime() + " - " + appointment.getTimeSlot().getEndTime();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Appointment Confirmation - DocBook");
            helper.setText(
                    EmailTemplateUtil.buildBookingConfirmationEmail(
                            patientName, doctorName, specialization, date, time, hospital
                    ),
                    true
            );

            mailSender.send(message);
        } catch (Throwable e) {
            log.error("Failed to send booking confirmation email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendBookingRejection(AppointmentEntity appointment) {
        try {
            String to = appointment.getPatient().getEmail();
            String patientName = appointment.getPatient().getName();
            String doctorName = appointment.getDoctor().getUser().getName();
            String date = appointment.getTimeSlot().getDate().toString();
            String time = appointment.getTimeSlot().getStartTime() + " - " + appointment.getTimeSlot().getEndTime();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Appointment Rejected - DocBook");
            helper.setText(
                    EmailTemplateUtil.buildBookingRejectionEmail(
                            patientName, doctorName, date, time
                    ),
                    true
            );

            mailSender.send(message);
        } catch (Throwable e) {
            log.error("Failed to send booking rejection email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendBookingAccepted(AppointmentEntity appointment) {
        try {
            String to = appointment.getPatient().getEmail();
            String doctorName = appointment.getDoctor().getUser().getName();
            String patientName = appointment.getPatient().getName();
            String specialization = appointment.getDoctor().getSpecialization() != null ? appointment.getDoctor().getSpecialization().name() : "GENERAL";
            String hospital = appointment.getDoctor().getHospital() != null ? appointment.getDoctor().getHospital() : "DocBook Clinic";
            String date = appointment.getTimeSlot().getDate().toString();
            String time = appointment.getTimeSlot().getStartTime() + " - " + appointment.getTimeSlot().getEndTime();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Appointment Accepted - DocBook");
            helper.setText(
                    EmailTemplateUtil.buildBookingAcceptedEmail(
                            patientName, doctorName, specialization, date, time, hospital
                    ),
                    true
            );

            mailSender.send(message);
        } catch (Throwable e) {
            log.error("Failed to send booking accepted email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendCancellationEmail(AppointmentEntity appointment) {
        try {
            String to = appointment.getPatient().getEmail();
            String patientName = appointment.getPatient().getName();
            String doctorName = appointment.getDoctor().getUser().getName();
            String date = appointment.getTimeSlot().getDate().toString();
            String time = appointment.getTimeSlot().getStartTime() + " - " + appointment.getTimeSlot().getEndTime();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Appointment Cancelled - DocBook");
            helper.setText(
                    EmailTemplateUtil.buildCancellationEmail(
                            patientName, doctorName, date, time
                    ),
                    true
            );

            mailSender.send(message);
        } catch (Throwable e) {
            log.error("Failed to send cancellation email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPaymentReceipt(PaymentEntity payment) {
        try {
            String to = payment.getAppointment().getPatient().getEmail();
            String patientName = payment.getAppointment().getPatient().getName();
            String doctorName = payment.getAppointment().getDoctor().getUser().getName();
            double amount = payment.getAmount();
            String orderId = payment.getRazorpayOrderId();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Payment Receipt - DocBook");
            helper.setText(
                    EmailTemplateUtil.buildPaymentReceiptEmail(
                            patientName, doctorName, amount, orderId
                    ),
                    true
            );

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send payment receipt email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendAppointmentReminder(AppointmentEntity appointment) {
        try {
            String to = appointment.getPatient().getEmail();
            String patientName = appointment.getPatient().getName();
            String doctorName = appointment.getDoctor().getUser().getName();
            String hospital = appointment.getDoctor().getHospital();
            String date = appointment.getTimeSlot().getDate().toString();
            String time = appointment.getTimeSlot().getStartTime() + " - " + appointment.getTimeSlot().getEndTime();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Appointment Reminder - DocBook");
            helper.setText(
                    EmailTemplateUtil.buildReminderEmail(
                            patientName, doctorName, date, time, hospital
                    ),
                    true
            );

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send appointment reminder email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordResetOtp(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Password Reset - DocBook");
            helper.setText(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                    "<div style='background: linear-gradient(135deg, #1a2b4a, #2d3748); padding: 30px; border-radius: 16px; text-align: center;'>" +
                        "<h1 style='color: #4fbdba; margin: 0;'>DocBook</h1>" +
                        "<p style='color: #cbd5e0; margin-top: 8px;'>Password Reset Request</p>" +
                    "</div>" +
                    "<div style='background: #ffffff; padding: 30px; border-radius: 16px; margin-top: 16px; border: 1px solid #e2e8f0;'>" +
                        "<p style='color: #2d3748; font-size: 16px;'>Hello,</p>" +
                        "<p style='color: #4a5568;'>You requested to reset your password. Use the following OTP to proceed:</p>" +
                        "<div style='background: #f7fafc; padding: 20px; border-radius: 12px; text-align: center; margin: 24px 0;'>" +
                            "<span style='font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #1a2b4a;'>" + otp + "</span>" +
                        "</div>" +
                        "<p style='color: #718096; font-size: 14px;'>This OTP is valid for <strong>10 minutes</strong>. If you didn't request this, please ignore this email.</p>" +
                    "</div>" +
                    "<p style='text-align: center; color: #a0aec0; font-size: 12px; margin-top: 16px;'>&copy; 2026 DocBook Healthcare. All rights reserved.</p>" +
                "</div>",
                true
            );

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send password reset OTP email to {}: {}", toEmail, e.getMessage());
        }
    }
}
