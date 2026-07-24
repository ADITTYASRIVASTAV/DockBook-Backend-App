package com.Aditya.DocBookApp.Service;

import com.Aditya.DocBookApp.DTO.SlotRequest;
import com.Aditya.DocBookApp.DTO.SlotResponse;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotService
{
    SlotResponse createSlot(SlotRequest request, String doctorEmail);

    List<SlotResponse> getDoctorSlots(String doctorEmail);

    List<SlotResponse> getAvailableSlots(Long doctorId, LocalDate date);

    void deleteSlot(Long slotId, String doctorEmail);
}
