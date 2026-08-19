package com.sms.service;

import com.sms.entity.Department;
import com.sms.entity.Designation;
import com.sms.entity.Employee;
import com.sms.entity.SalaryRecord;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.DesignationRepository;
import com.sms.repository.EmployeeRepository;
import com.sms.repository.SalaryRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for streaming CSV exports of employee and salary data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvExportService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;

    /**
     * Export all active employees to CSV string.
     */
    public String exportEmployeesToCsv() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        // CSV Header
        writer.println("Employee ID,First Name,Last Name,Email,Phone,Department,Designation,Hire Date,Country,Currency,Status");

        Map<Long, String> deptMap = departmentRepository.findAll().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        Map<Long, String> desigMap = designationRepository.findAll().stream()
                .collect(Collectors.toMap(Designation::getId, Designation::getName));

        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .toList();

        for (Employee e : employees) {
            String dept = e.getDepartmentId() != null ? deptMap.getOrDefault(e.getDepartmentId(), "") : "";
            String desig = e.getDesignationId() != null ? desigMap.getOrDefault(e.getDesignationId(), "") : "";

            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    escapeCsv(e.getEmployeeId()),
                    escapeCsv(e.getFirstName()),
                    escapeCsv(e.getLastName()),
                    escapeCsv(e.getEmail()),
                    escapeCsv(e.getPhone()),
                    escapeCsv(dept),
                    escapeCsv(desig),
                    e.getHireDate() != null ? e.getHireDate().toString() : "",
                    escapeCsv(e.getCountry()),
                    escapeCsv(e.getCurrency()),
                    escapeCsv(e.getStatus())
            );
        }

        writer.flush();
        return sw.toString();
    }

    /**
     * Export all salary records to CSV string.
     */
    public String exportSalariesToCsv() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        // CSV Header
        writer.println("Salary Record ID,Employee ID,Employee Name,Base Salary,Allowances,Gross Salary,Deductions,Tax,Net Salary,Currency,Effective Date,Status");

        Map<Long, Employee> empMap = employeeRepository.findAll().stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));

        List<SalaryRecord> salaries = salaryRecordRepository.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                .toList();

        for (SalaryRecord s : salaries) {
            Employee emp = empMap.get(s.getEmployeeId());
            String empName = emp != null ? emp.getFirstName() + " " + emp.getLastName() : "Unknown";
            String empCode = emp != null ? emp.getEmployeeId() : String.valueOf(s.getEmployeeId());

            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    s.getId(),
                    escapeCsv(empCode),
                    escapeCsv(empName),
                    s.getBaseSalary(),
                    s.getAllowances(),
                    s.getGrossSalary(),
                    s.getDeductions(),
                    s.getTax(),
                    s.getNetSalary(),
                    escapeCsv(s.getCurrency()),
                    s.getEffectiveDate() != null ? s.getEffectiveDate().toString() : "",
                    escapeCsv(s.getStatus())
            );
        }

        writer.flush();
        return sw.toString();
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace("\"", "\"\"");
    }
}
