package com.sms.service;

import com.sms.dto.DashboardMetricsDto;
import com.sms.dto.DepartmentDistributionDto;
import com.sms.dto.PayEquityDto;
import com.sms.entity.Department;
import com.sms.entity.Designation;
import com.sms.entity.Employee;
import com.sms.entity.SalaryRecord;
import com.sms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for HR analytics, executive KPIs, department distributions, and pay equity calculations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;

    /**
     * Get executive dashboard metrics.
     */
    public DashboardMetricsDto getDashboardMetrics() {
        List<Employee> allEmployees = employeeRepository.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .toList();

        List<SalaryRecord> allActiveSalaries = salaryRecordRepository.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()) && "ACTIVE".equalsIgnoreCase(s.getStatus()))
                .toList();

        long totalCount = allEmployees.size();
        long activeCount = allEmployees.stream().filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus())).count();
        long inactiveCount = totalCount - activeCount;

        long deptCount = departmentRepository.count();
        long desigCount = designationRepository.count();
        long pendingApprovals = salaryRecordRepository.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()) && "INACTIVE".equalsIgnoreCase(s.getStatus()) && s.getApprovedAt() == null)
                .count();

        // Total payroll by currency
        Map<String, BigDecimal> monthlyPayroll = new HashMap<>();
        Map<String, BigDecimal> annualPayroll = new HashMap<>();
        Map<String, BigDecimal> taxByCurrency = new HashMap<>();

        for (SalaryRecord s : allActiveSalaries) {
            String cur = s.getCurrency() != null ? s.getCurrency() : "USD";
            BigDecimal gross = s.getGrossSalary() != null ? s.getGrossSalary() : BigDecimal.ZERO;
            BigDecimal tax = s.getTax() != null ? s.getTax() : BigDecimal.ZERO;

            monthlyPayroll.put(cur, monthlyPayroll.getOrDefault(cur, BigDecimal.ZERO).add(gross));
            annualPayroll.put(cur, annualPayroll.getOrDefault(cur, BigDecimal.ZERO).add(gross.multiply(new BigDecimal("12"))));
            taxByCurrency.put(cur, taxByCurrency.getOrDefault(cur, BigDecimal.ZERO).add(tax));
        }

        // Headcount by Department
        Map<Long, Department> deptMap = departmentRepository.findAll().stream()
                .collect(Collectors.toMap(Department::getId, d -> d));

        Map<Long, Long> deptCounts = allEmployees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartmentId, Collectors.counting()));

        List<DashboardMetricsDto.HeadcountByDepartmentDto> headcountByDept = deptCounts.entrySet().stream()
                .map(e -> {
                    Department dept = deptMap.get(e.getKey());
                    String name = dept != null ? dept.getName() : "Department " + e.getKey();
                    double pct = totalCount > 0 ? (e.getValue() * 100.0) / totalCount : 0.0;
                    return DashboardMetricsDto.HeadcountByDepartmentDto.builder()
                            .departmentId(e.getKey())
                            .departmentName(name)
                            .count(e.getValue())
                            .percentage(Math.round(pct * 10.0) / 10.0)
                            .build();
                })
                .sorted(Comparator.comparingLong(DashboardMetricsDto.HeadcountByDepartmentDto::getCount).reversed())
                .toList();

        // Headcount by Country
        Map<String, Long> countryCounts = allEmployees.stream()
                .collect(Collectors.groupingBy(Employee::getCountry, Collectors.counting()));

        List<DashboardMetricsDto.HeadcountByCountryDto> headcountByCountry = countryCounts.entrySet().stream()
                .map(e -> {
                    double pct = totalCount > 0 ? (e.getValue() * 100.0) / totalCount : 0.0;
                    return DashboardMetricsDto.HeadcountByCountryDto.builder()
                            .country(e.getKey())
                            .currency(getCurrencyForCountry(e.getKey()))
                            .count(e.getValue())
                            .percentage(Math.round(pct * 10.0) / 10.0)
                            .build();
                })
                .sorted(Comparator.comparingLong(DashboardMetricsDto.HeadcountByCountryDto::getCount).reversed())
                .toList();

        // Headcount by Status
        Map<String, Long> statusCounts = allEmployees.stream()
                .collect(Collectors.groupingBy(Employee::getStatus, Collectors.counting()));

        List<DashboardMetricsDto.HeadcountByStatusDto> headcountByStatus = statusCounts.entrySet().stream()
                .map(e -> DashboardMetricsDto.HeadcountByStatusDto.builder()
                        .status(e.getKey())
                        .count(e.getValue())
                        .build())
                .toList();

        return DashboardMetricsDto.builder()
                .totalEmployees(totalCount)
                .activeEmployees(activeCount)
                .inactiveEmployees(inactiveCount)
                .departmentsCount(deptCount)
                .designationsCount(desigCount)
                .pendingSalaryApprovals(pendingApprovals)
                .totalMonthlyPayrollByCurrency(monthlyPayroll)
                .totalAnnualPayrollByCurrency(annualPayroll)
                .totalTaxCollectedByCurrency(taxByCurrency)
                .headcountByDepartment(headcountByDept)
                .headcountByCountry(headcountByCountry)
                .headcountByStatus(headcountByStatus)
                .build();
    }

    /**
     * Get department salary distribution statistics.
     */
    public List<DepartmentDistributionDto> getDepartmentDistributions() {
        List<Department> departments = departmentRepository.findAll();
        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .toList();

        Map<Long, SalaryRecord> activeSalaryByEmpId = salaryRecordRepository.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()) && "ACTIVE".equalsIgnoreCase(s.getStatus()))
                .collect(Collectors.toMap(SalaryRecord::getEmployeeId, s -> s, (s1, s2) -> s1));

        Map<Long, List<Employee>> empsByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartmentId));

        List<DepartmentDistributionDto> result = new ArrayList<>();

        for (Department dept : departments) {
            List<Employee> deptEmps = empsByDept.getOrDefault(dept.getId(), Collections.emptyList());
            if (deptEmps.isEmpty()) continue;

            List<BigDecimal> salaries = deptEmps.stream()
                    .map(e -> activeSalaryByEmpId.get(e.getId()))
                    .filter(Objects::nonNull)
                    .map(SalaryRecord::getGrossSalary)
                    .sorted()
                    .toList();

            if (salaries.isEmpty()) continue;

            BigDecimal min = salaries.get(0);
            BigDecimal max = salaries.get(salaries.size() - 1);
            BigDecimal sumGross = BigDecimal.ZERO;
            BigDecimal sumTax = BigDecimal.ZERO;
            BigDecimal sumNet = BigDecimal.ZERO;

            for (Employee e : deptEmps) {
                SalaryRecord s = activeSalaryByEmpId.get(e.getId());
                if (s != null) {
                    sumGross = sumGross.add(s.getGrossSalary() != null ? s.getGrossSalary() : BigDecimal.ZERO);
                    sumTax = sumTax.add(s.getTax() != null ? s.getTax() : BigDecimal.ZERO);
                    sumNet = sumNet.add(s.getNetSalary() != null ? s.getNetSalary() : BigDecimal.ZERO);
                }
            }

            BigDecimal avg = sumGross.divide(new BigDecimal(salaries.size()), 2, RoundingMode.HALF_UP);
            BigDecimal median = salaries.get(salaries.size() / 2);
            BigDecimal p25 = salaries.get((int) Math.floor(salaries.size() * 0.25));
            BigDecimal p75 = salaries.get((int) Math.floor(salaries.size() * 0.75));

            result.add(DepartmentDistributionDto.builder()
                    .departmentId(dept.getId())
                    .departmentName(dept.getName())
                    .currency("USD")
                    .employeeCount(deptEmps.size())
                    .minSalary(min)
                    .maxSalary(max)
                    .averageSalary(avg)
                    .medianSalary(median)
                    .p25Salary(p25)
                    .p75Salary(p75)
                    .totalGrossSalary(sumGross)
                    .totalTax(sumTax)
                    .totalNetSalary(sumNet)
                    .build());
        }

        return result;
    }

    /**
     * Get pay equity analysis by designation.
     */
    public List<PayEquityDto> getPayEquityAnalysis() {
        List<Designation> designations = designationRepository.findAll();
        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .toList();

        Map<Long, SalaryRecord> activeSalaryByEmpId = salaryRecordRepository.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()) && "ACTIVE".equalsIgnoreCase(s.getStatus()))
                .collect(Collectors.toMap(SalaryRecord::getEmployeeId, s -> s, (s1, s2) -> s1));

        Map<Long, List<Employee>> empsByDesig = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDesignationId));

        List<PayEquityDto> result = new ArrayList<>();

        for (Designation desig : designations) {
            List<Employee> desigEmps = empsByDesig.getOrDefault(desig.getId(), Collections.emptyList());
            if (desigEmps.isEmpty()) continue;

            List<BigDecimal> salaries = desigEmps.stream()
                    .map(e -> activeSalaryByEmpId.get(e.getId()))
                    .filter(Objects::nonNull)
                    .map(SalaryRecord::getGrossSalary)
                    .sorted()
                    .toList();

            if (salaries.isEmpty()) continue;

            BigDecimal min = salaries.get(0);
            BigDecimal max = salaries.get(salaries.size() - 1);
            BigDecimal sum = salaries.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = sum.divide(new BigDecimal(salaries.size()), 2, RoundingMode.HALF_UP);
            BigDecimal median = salaries.get(salaries.size() / 2);

            BigDecimal spreadRatio = min.compareTo(BigDecimal.ZERO) > 0 
                    ? max.divide(min, 2, RoundingMode.HALF_UP) 
                    : BigDecimal.ONE;

            result.add(PayEquityDto.builder()
                    .designationId(desig.getId())
                    .designationTitle(desig.getTitle())
                    .currency("USD")
                    .employeeCount(desigEmps.size())
                    .minSalary(min)
                    .maxSalary(max)
                    .averageSalary(avg)
                    .medianSalary(median)
                    .salarySpreadRatio(spreadRatio)
                    .build());
        }

        return result;
    }

    private String getCurrencyForCountry(String country) {
        if (country == null) return "USD";
        return switch (country.toUpperCase()) {
            case "UNITED KINGDOM", "UK", "GB" -> "GBP";
            case "INDIA", "IN" -> "INR";
            case "GERMANY", "FRANCE", "DE", "FR", "EU" -> "EUR";
            case "CANADA", "CA" -> "CAD";
            case "AUSTRALIA", "AU" -> "AUD";
            case "JAPAN", "JP" -> "JPY";
            case "SINGAPORE", "SG" -> "SGD";
            default -> "USD";
        };
    }
}
