package com.worktrack.dto;

import com.worktrack.model.LeaveRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDto {

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;


    private EmployeeDto employeeDto;

    private String feedback;

    private LeaveRequest.LeaveStatus status;

}
