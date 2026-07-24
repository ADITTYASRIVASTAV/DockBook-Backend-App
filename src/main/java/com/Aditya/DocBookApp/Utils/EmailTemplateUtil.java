package com.Aditya.DocBookApp.Utils;

public class EmailTemplateUtil {

    public static String buildOtpEmail(String otp) {
        return """
                <div style="font-family: Arial, sans-serif; background-color:#f4f4f4; padding:20px;">
                    <div style="max-width:600px; margin:auto; background:#ffffff; padding:20px; border-radius:10px;">
                        <h2 style="color:#2c3e50;">DocBook - OTP Verification</h2>
                        <p>Your OTP for verification is:</p>
                        <h1 style="color:#27ae60;">%s</h1>
                        <p>This OTP is valid for a short time. Do not share it with anyone.</p>
                        <hr/>
                        <p style="font-size:12px; color:#888;">© DocBook</p>
                    </div>
                </div>
                """.formatted(otp);
    }

    public static String buildBookingConfirmationEmail(
            String patientName,
            String doctorName,
            String specialization,
            String date,
            String time,
            String hospital
    ) {
        return """
                <div style="font-family: Arial, sans-serif; background:#f4f6f8; padding:20px;">
                    <div style="max-width:600px; margin:auto; background:#ffffff; padding:20px; border-radius:10px;">
                        <h2 style="color:#2980b9;">Appointment Booked Successfully</h2>
                        <p>Dear %s,</p>
                        <p>Your appointment has been successfully booked.</p>
                        <table style="width:100%%; border-collapse:collapse;">
                            <tr><td><strong>Doctor:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Specialization:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Hospital:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Date:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Time:</strong></td><td>%s</td></tr>
                        </table>
                        <p style="margin-top:20px;">Please arrive 10 minutes early.</p>
                        <hr/>
                        <p style="font-size:12px; color:#999;">© DocBook</p>
                    </div>
                </div>
                """.formatted(patientName, doctorName, specialization, hospital, date, time);
    }

    public static String buildBookingRejectionEmail(
            String patientName,
            String doctorName,
            String date,
            String time
    ) {
        return """
                <div style="font-family: Arial, sans-serif; background:#f4f6f8; padding:20px;">
                    <div style="max-width:600px; margin:auto; background:#ffffff; padding:20px; border-radius:10px;">
                        <h2 style="color:#e74c3c;">Appointment Rejected</h2>
                        <p>Dear %s,</p>
                        <p>We regret to inform you that your appointment has been rejected.</p>
                        <table style="width:100%%;">
                            <tr><td><strong>Doctor:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Date:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Time:</strong></td><td>%s</td></tr>
                        </table>
                        <p>Please try booking another slot.</p>
                        <hr/>
                        <p style="font-size:12px; color:#999;">© DocBook</p>
                    </div>
                </div>
                """.formatted(patientName, doctorName, date, time);
    }

    public static String buildPaymentReceiptEmail(
            String patientName,
            String doctorName,
            double amount,
            String razorpayOrderId
    ) {
        return """
                <div style="font-family: Arial, sans-serif; background:#f4f6f8; padding:20px;">
                    <div style="max-width:600px; margin:auto; background:#ffffff; padding:20px; border-radius:10px;">
                        <h2 style="color:#27ae60;">Payment Successful</h2>
                        <p>Dear %s,</p>
                        <p>Your payment has been successfully processed.</p>
                        <table style="width:100%%;">
                            <tr><td><strong>Doctor:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Amount Paid:</strong></td><td>₹%.2f</td></tr>
                            <tr><td><strong>Order ID:</strong></td><td>%s</td></tr>
                        </table>
                        <p>Thank you for using DocBook.</p>
                        <hr/>
                        <p style="font-size:12px; color:#999;">© DocBook</p>
                    </div>
                </div>
                """.formatted(patientName, doctorName, amount, razorpayOrderId);
    }

    public static String buildBookingAcceptedEmail(
            String patientName,
            String doctorName,
            String specialization,
            String date,
            String time,
            String hospital
    ) {
        return """
                <div style="font-family: Arial, sans-serif; background:#f4f6f8; padding:20px;">
                    <div style="max-width:600px; margin:auto; background:#ffffff; padding:20px; border-radius:10px;">
                        <h2 style="color:#27ae60;">Appointment Accepted</h2>
                        <p>Dear %s,</p>
                        <p>Your appointment has been <strong>accepted</strong> by the doctor.</p>
                        <table style="width:100%%; border-collapse:collapse;">
                            <tr><td><strong>Doctor:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Specialization:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Hospital:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Date:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Time:</strong></td><td>%s</td></tr>
                        </table>
                        <p style="margin-top:20px;">Please arrive 10 minutes early.</p>
                        <hr/>
                        <p style="font-size:12px; color:#999;">© DocBook</p>
                    </div>
                </div>
                """.formatted(patientName, doctorName, specialization, hospital, date, time);
    }

    public static String buildCancellationEmail(
            String patientName,
            String doctorName,
            String date,
            String time
    ) {
        return """
                <div style="font-family: Arial, sans-serif; background:#f4f6f8; padding:20px;">
                    <div style="max-width:600px; margin:auto; background:#ffffff; padding:20px; border-radius:10px;">
                        <h2 style="color:#e67e22;">Appointment Cancelled</h2>
                        <p>Dear %s,</p>
                        <p>Your appointment has been <strong>cancelled</strong> successfully.</p>
                        <table style="width:100%%;">
                            <tr><td><strong>Doctor:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Date:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Time:</strong></td><td>%s</td></tr>
                        </table>
                        <p>You can book a new appointment anytime.</p>
                        <hr/>
                        <p style="font-size:12px; color:#999;">© DocBook</p>
                    </div>
                </div>
                """.formatted(patientName, doctorName, date, time);
    }

    public static String buildReminderEmail(
            String patientName,
            String doctorName,
            String date,
            String time,
            String hospital
    ) {
        return """
                <div style="font-family: Arial, sans-serif; background:#f4f6f8; padding:20px;">
                    <div style="max-width:600px; margin:auto; background:#ffffff; padding:20px; border-radius:10px;">
                        <h2 style="color:#f39c12;">Appointment Reminder</h2>
                        <p>Dear %s,</p>
                        <p>This is a reminder for your upcoming appointment.</p>
                        <table style="width:100%%;">
                            <tr><td><strong>Doctor:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Hospital:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Date:</strong></td><td>%s</td></tr>
                            <tr><td><strong>Time:</strong></td><td>%s</td></tr>
                        </table>
                        <p>Please be on time.</p>
                        <hr/>
                        <p style="font-size:12px; color:#999;">© DocBook</p>
                    </div>
                </div>
                """.formatted(patientName, doctorName, hospital, date, time);
    }
}
