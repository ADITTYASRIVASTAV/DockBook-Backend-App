package com.Aditya.DocBookApp.ServiceImpl;

import com.Aditya.DocBookApp.DTO.PaymentRequest;
import com.Aditya.DocBookApp.DTO.PaymentResponse;
import com.Aditya.DocBookApp.DTO.PaymentVerifyRequest;
import com.Aditya.DocBookApp.Entity.AppointmentEntity;
import com.Aditya.DocBookApp.Entity.PaymentEntity;
import com.Aditya.DocBookApp.Enum.AppointmentStatus;
import com.Aditya.DocBookApp.Enum.PaymentStatus;
import com.Aditya.DocBookApp.Exception.BadRequestException;
import com.Aditya.DocBookApp.Exception.ResourceNotFoundException;
import com.Aditya.DocBookApp.Repository.AppointmentRepository;
import com.Aditya.DocBookApp.Repository.PaymentRepository;
import com.Aditya.DocBookApp.Service.EmailService;
import com.Aditya.DocBookApp.Service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService
{

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final RazorpayClient razorpayClient;
    private final EmailService emailService;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public PaymentResponse createOrder(PaymentRequest request, String patientEmail)
    {
        AppointmentEntity appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + request.getAppointmentId()));

        if(!appointment.getPatient().getEmail().equals(patientEmail))
        {
            throw new BadRequestException("You can only create payment for your own appointments");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING &&
                appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Payment can only be created for appointments with PENDING or CONFIRMED status. Current status: " + appointment.getStatus());
        }

        if (paymentRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            throw new BadRequestException("Payment already exists for this appointment");
        }

        try
        {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (appointment.getDoctor().getFee() * 100));
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "appointment_" + appointment.getId());

            Order order = razorpayClient.orders.create(orderRequest);
            PaymentEntity payment = new PaymentEntity();
            payment.setAppointment(appointment);
            payment.setAmount(appointment.getDoctor().getFee());
            payment.setRazorpayOrderId(order.get("id"));
            payment.setStatus(PaymentStatus.CREATED);

            PaymentEntity savedPayment = paymentRepository.save(payment);
            return PaymentResponse.builder()
                    .id(savedPayment.getId())
                    .appointmentId(appointment.getId())
                    .amount(savedPayment.getAmount())
                    .razorpayOrderId(savedPayment.getRazorpayOrderId())
                    .status(savedPayment.getStatus())
                    .message("Order created successfully")
                    .build();
        }
        catch (RazorpayException e)
        {
            throw new BadRequestException("Failed to create Razorpay order: " + e.getMessage());
        }

    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(PaymentVerifyRequest request)
    {
        PaymentEntity payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: "
                        + request.getRazorpayOrderId()));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment already verified");
        }

        try
        {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            AppointmentEntity appointment = payment.getAppointment();
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointmentRepository.save(appointment);

            emailService.sendPaymentReceipt(payment);

            return PaymentResponse.builder()
                    .id(payment.getId())
                    .appointmentId(appointment.getId())
                    .amount(payment.getAmount())
                    .razorpayOrderId(payment.getRazorpayOrderId())
                    .status(payment.getStatus())
                    .message("Payment verified successfully. Appointment confirmed.")
                    .build();
        }
        catch(RazorpayException e)
        {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BadRequestException("Payment verification failed: " + e.getMessage());
        }
    }
}
