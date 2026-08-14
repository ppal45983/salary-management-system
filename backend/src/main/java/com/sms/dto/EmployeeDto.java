package com.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating or updating an Employee.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    private Long id;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone must be valid")
    private String phone;

    private LocalDate dateOfBirth;

    private String gender;

    @NotNull(message = "Department is required")
    private Long departmentId;

    @NotNull(message = "Designation is required")
    private Long designationId;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    @NotBlank(message = "Employment type is required")
    private String employmentType;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Currency is required")
    private String currency;

    private String taxId;
    private String bankAccount;
    private String bankCode;

    @NotBlank(message = "Status is required")
    private String status;

    private LocalDate terminationDate;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private Long managerId;
}
