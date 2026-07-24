package com.Aditya.DocBookApp.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest
{
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    @NotNull(message = "Slot ID is required")
    private Long slotId;
    private String problemDescription;
}
