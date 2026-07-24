package com.Aditya.DocBookApp.scheduler;

import com.Aditya.DocBookApp.Entity.AppointmentEntity;
import com.Aditya.DocBookApp.Enum.AppointmentStatus;
import com.Aditya.DocBookApp.Repository.AppointmentRepository;
import com.Aditya.DocBookApp.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AppointmentReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    private final Set<Long> remindedAppointments = ConcurrentHashMap.newKeySet();

    @Scheduled(fixedRate = 60000)
    public void sendReminders() {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime nextHour = now.plusHours(1);

        List<AppointmentEntity> appointments =
                appointmentRepository.findUpcomingAppointments(
                        AppointmentStatus.CONFIRMED,
                        today,
                        now,
                        nextHour
                );

        for (AppointmentEntity appointment : appointments) {
            if (!remindedAppointments.contains(appointment.getId())) {
                emailService.sendAppointmentReminder(appointment);
                remindedAppointments.add(appointment.getId());
            }
        }
    }

    // Clears the reminded set daily at midnight to prevent memory leak
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupRemindedAppointments() {
        remindedAppointments.clear();
    }
}
