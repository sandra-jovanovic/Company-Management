package com.worktrack.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeDto {

    @Valid

    @NotNull(message = "First name is required.")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstname;

    @NotNull(message = "Last name is required.")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String lastname;

    @NotNull(message = "Email is required.")
    @Email(message = "Email should be valid.")
    private String email;

}
