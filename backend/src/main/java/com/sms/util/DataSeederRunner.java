package com.sms.util;

import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.TaxCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Automated Data Seeder to generate realistic test data for 10,000 employees,
 * departments, designations, salary records, progressive taxes, and admin users.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeederRunner implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final TaxBracketRepository taxBracketRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaxCalculationService taxCalculationService;

    private static final String[] FIRST_NAMES = {
            "James", "Mary", "Robert", "Patricia", "John", "Jennifer", "Michael", "Linda", "David", "Elizabeth",
            "William", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen",
            "Christopher", "Nancy", "Daniel", "Lisa", "Matthew", "Betty", "Anthony", "Margaret", "Mark", "Sandra",
            "Donald", "Ashley", "Steven", "Kimberly", "Paul", "Emily", "Andrew", "Donna", "Joshua", "Michelle",
            "Kenneth", "Dorothy", "Kevin", "Carol", "Brian", "Amanda", "George", "Melissa", "Edward", "Deborah",
            "Aarav", "Priya", "Rahul", "Ananya", "Rohan", "Sneha", "Aditya", "Pooja", "Vikram", "Neha",
            "Wei", "Yuki", "Kenji", "Sakura", "Hiroshi", "Mei", "Jin", "Aoi", "Daiki", "Hina",
            "Lukas", "Emma", "Maximilian", "Hannah", "Felix", "Mia", "Leon", "Sophie", "Paul", "Anna",
            "Alexandre", "Camille", "Hugo", "Manon", "Antoine", "Lea", "Thomas", "Chloe", "Nicolas", "Ines"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
            "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
            "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
            "Sharma", "Patel", "Verma", "Gupta", "Reddy", "Mehta", "Iyer", "Nair", "Kapoor", "Chopra",
            "Tanaka", "Sato", "Suzuki", "Takahashi", "Watanabe", "Ito", "Yamamoto", "Nakamura", "Kobayashi", "Kato",
            "Mueller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker", "Schulz", "Hoffmann",
            "Dubois", "Lambert", "Moreau", "Fournier", "Girard", "Bonnet", "Dupont", "Fontaine", "Rousseau", "Vincent"
    };

    private static final String[][] COUNTRIES = {
            {"United States", "USD", "US"},
            {"United Kingdom", "GBP", "GB"},
            {"India", "INR", "IN"},
            {"Germany", "EUR", "DE"},
            {"France", "EUR", "FR"},
            {"Canada", "CAD", "CA"},
            {"Australia", "AUD", "AU"},
            {"Japan", "JPY", "JP"},
            {"Singapore", "SGD", "SG"}
    };

    @Override
    public void run(String... args) {
        // Only seed if empty
        if (departmentRepository.count() == 0) {
            log.info("Starting master data & admin user initialization...");
            seedMastersAndUsers();
        }

        boolean forceSeed = Arrays.asList(args).contains("--seed-10k");
        if (forceSeed || employeeRepository.count() < 100) {
            log.info("Seeding realistic employee records (target: 10,000)...");
            seedEmployeesAndSalaries(10000);
            log.info("Data seeding completed successfully! Total Employees: {}", employeeRepository.count());
        }
    }

    @Transactional
    public void seedMastersAndUsers() {
        // Create Admin User
        if (userRepository.findByUsername("hr_manager").isEmpty()) {
            User admin = new User();
            admin.setUsername("hr_manager");
            admin.setEmail("hr_manager@acme.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole("HR_MANAGER");
            admin.setIsActive(true);
            admin.setAccountLocked(false);
            admin.setFailedLoginAttempts(0);
            userRepository.save(admin);
            log.info("Admin user created: hr_manager / admin123");
        }

        // Create Departments
        String[][] depts = {
                {"Engineering", "ENG", "Software engineering and cloud infrastructure", "15000000"},
                {"Human Resources", "HR", "People operations and talent acquisition", "3000000"},
                {"Finance", "FIN", "Financial planning, accounting, and payroll", "4500000"},
                {"Sales", "SLS", "Global enterprise and mid-market sales", "12000000"},
                {"Marketing", "MKT", "Product marketing, brand, and demand generation", "6000000"},
                {"Product Management", "PRD", "Product discovery and roadmap execution", "5000000"},
                {"Legal & Compliance", "LGL", "Corporate legal counsel and compliance", "2500000"},
                {"Customer Success", "CS", "Client onboarding and technical support", "4000000"},
                {"Operations", "OPS", "Business operations and logistics", "3500000"}
        };

        for (String[] d : depts) {
            Department dept = new Department();
            dept.setName(d[0]);
            dept.setDepartmentCode(d[1]);
            dept.setDescription(d[2]);
            dept.setBudget(new BigDecimal(d[3]));
            dept.setLocation("Global");
            dept.setIsActive(true);
            departmentRepository.save(dept);
        }

        // Create Designations
        Object[][] desigs = {
                {"Associate Software Engineer", "Junior engineer", "L1", "45000", "75000", 1L},
                {"Software Engineer", "Core developer", "L2", "70000", "115000", 1L},
                {"Senior Software Engineer", "Senior fullstack engineer", "L3", "110000", "165000", 1L},
                {"Staff Engineer", "Principal technical lead", "L4", "150000", "220000", 1L},
                {"Engineering Manager", "People leader for engineering team", "M1", "140000", "210000", 1L},
                {"HR Specialist", "People partner", "L2", "50000", "80000", 2L},
                {"HR Manager", "Senior HR manager", "M1", "90000", "140000", 2L},
                {"Financial Analyst", "Corporate finance", "L2", "60000", "95000", 3L},
                {"Finance Manager", "Accounting manager", "M1", "100000", "150000", 3L},
                {"Account Executive", "Sales executive", "L2", "65000", "130000", 4L},
                {"Sales Director", "Regional sales leader", "D1", "150000", "250000", 4L},
                {"Product Manager", "Core product manager", "L3", "110000", "170000", 6L},
                {"Customer Success Manager", "Key accounts manager", "L2", "60000", "95000", 8L},
                {"VP of Engineering", "Executive engineering head", "VP", "220000", "350000", 1L}
        };

        for (Object[] d : desigs) {
            Designation desig = new Designation();
            desig.setName((String) d[0]);
            desig.setDescription((String) d[1]);
            desig.setLevel((String) d[2]);
            desig.setMinSalary(new BigDecimal((String) d[3]));
            desig.setMaxSalary(new BigDecimal((String) d[4]));
            desig.setDepartmentId((Long) d[5]);
            desig.setIsActive(true);
            designationRepository.save(desig);
        }

        // Seed Tax Brackets for 9 countries
        seedTaxBrackets();
    }

    private void seedTaxBrackets() {
        Object[][] taxData = {
                // US
                {"United States", 2024, "0", "11600", "10.0", "USD"},
                {"United States", 2024, "11600", "47150", "12.0", "USD"},
                {"United States", 2024, "47150", "100525", "22.0", "USD"},
                {"United States", 2024, "100525", "191950", "24.0", "USD"},
                {"United States", 2024, "191950", "999999999", "32.0", "USD"},
                // UK
                {"United Kingdom", 2024, "0", "12570", "0.0", "GBP"},
                {"United Kingdom", 2024, "12570", "50270", "20.0", "GBP"},
                {"United Kingdom", 2024, "50270", "125140", "40.0", "GBP"},
                {"United Kingdom", 2024, "125140", "999999999", "45.0", "GBP"},
                // India
                {"India", 2024, "0", "300000", "0.0", "INR"},
                {"India", 2024, "300000", "600000", "5.0", "INR"},
                {"India", 2024, "600000", "900000", "10.0", "INR"},
                {"India", 2024, "900000", "1200000", "15.0", "INR"},
                {"India", 2024, "1200000", "1500000", "20.0", "INR"},
                {"India", 2024, "1500000", "999999999", "30.0", "INR"},
                // Germany
                {"Germany", 2024, "0", "11604", "0.0", "EUR"},
                {"Germany", 2024, "11604", "66760", "14.0", "EUR"},
                {"Germany", 2024, "66760", "277825", "42.0", "EUR"},
                {"Germany", 2024, "277825", "999999999", "45.0", "EUR"}
        };

        for (Object[] t : taxData) {
            TaxBracket bracket = new TaxBracket();
            bracket.setCountry((String) t[0]);
            bracket.setTaxYear((Integer) t[1]);
            bracket.setIncomeFrom(new BigDecimal((String) t[2]));
            bracket.setIncomeTo(new BigDecimal((String) t[3]));
            bracket.setTaxRate(new BigDecimal((String) t[4]));
            bracket.setCurrency((String) t[5]);
            bracket.setEffectiveFrom(LocalDate.of(2024, 1, 1));
            bracket.setIsActive(true);
            taxBracketRepository.save(bracket);
        }
    }

    @Transactional
    public void seedEmployeesAndSalaries(int count) {
        List<Department> departments = departmentRepository.findAll();
        List<Designation> designations = designationRepository.findAll();
        Random random = new Random(42); // Deterministic seed

        List<Employee> employeeBatch = new ArrayList<>();
        List<SalaryRecord> salaryBatch = new ArrayList<>();
        List<SalaryHistory> historyBatch = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String empId = String.format("EMP-%05d", i);
            String email = String.format("%s.%s.%d@acme.com", firstName.toLowerCase(), lastName.toLowerCase(), i);

            String[] countryInfo = COUNTRIES[random.nextInt(COUNTRIES.length)];
            Department dept = departments.get(random.nextInt(departments.size()));
            Designation desig = designations.get(random.nextInt(designations.size()));

            Employee employee = new Employee();
            employee.setEmployeeId(empId);
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setPhone(String.format("+1-%03d-%03d-%04d", random.nextInt(900) + 100, random.nextInt(900) + 100, random.nextInt(9000) + 1000));
            employee.setDateOfBirth(LocalDate.of(1975 + random.nextInt(25), 1 + random.nextInt(12), 1 + random.nextInt(28)));
            employee.setGender(random.nextBoolean() ? "Male" : "Female");
            employee.setDepartmentId(dept.getId());
            employee.setDesignationId(desig.getId());
            employee.setHireDate(LocalDate.of(2018 + random.nextInt(6), 1 + random.nextInt(12), 1 + random.nextInt(28)));
            employee.setEmploymentType("FULL_TIME");
            employee.setCountry(countryInfo[0]);
            employee.setCurrency(countryInfo[1]);
            employee.setTaxId(String.format("TX-%09d", random.nextInt(1000000000)));
            employee.setBankAccount(String.format("ACCT-%010d", random.nextInt(1000000000)));
            employee.setBankCode("ACME-BANK");
            employee.setStatus(random.nextDouble() > 0.05 ? "ACTIVE" : "ON_LEAVE");
            employee.setCreatedBy("SYSTEM_SEEDER");
            employee.setIsActive(true);

            employeeBatch.add(employee);
        }

        // Save employees in chunks
        List<Employee> savedEmployees = employeeRepository.saveAll(employeeBatch);

        // Generate matching salary records and histories
        for (Employee emp : savedEmployees) {
            Designation desig = designationRepository.findById(emp.getDesignationId()).orElse(designations.get(0));
            BigDecimal minSal = desig.getMinSalary() != null ? desig.getMinSalary() : new BigDecimal("60000");
            BigDecimal maxSal = desig.getMaxSalary() != null ? desig.getMaxSalary() : new BigDecimal("120000");

            double spread = maxSal.subtract(minSal).doubleValue();
            double randSal = minSal.doubleValue() + (random.nextDouble() * spread);
            BigDecimal baseSalary = new BigDecimal(Math.round(randSal / 1000.0) * 1000.0);
            BigDecimal allowances = new BigDecimal(Math.round((baseSalary.doubleValue() * 0.12) / 100.0) * 100.0);
            BigDecimal deductions = new BigDecimal(Math.round((baseSalary.doubleValue() * 0.04) / 100.0) * 100.0);
            BigDecimal grossSalary = baseSalary.add(allowances);

            // Progressive Tax rate approximation
            double taxRate = "United States".equals(emp.getCountry()) ? 0.22 :
                             "United Kingdom".equals(emp.getCountry()) ? 0.20 :
                             "Germany".equals(emp.getCountry()) ? 0.25 : 0.15;
            BigDecimal tax = grossSalary.multiply(new BigDecimal(taxRate)).setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal netSalary = grossSalary.subtract(deductions).subtract(tax);

            SalaryRecord salary = new SalaryRecord();
            salary.setEmployeeId(emp.getId());
            salary.setBaseSalary(baseSalary);
            salary.setAllowances(allowances);
            salary.setDeductions(deductions);
            salary.setGrossSalary(grossSalary);
            salary.setTax(tax);
            salary.setNetSalary(netSalary);
            salary.setEffectiveDate(emp.getHireDate());
            salary.setStatus("ACTIVE");
            salary.setPayFrequency("MONTHLY");
            salary.setCurrency(emp.getCurrency());
            salary.setCreatedBy("SYSTEM_SEEDER");
            salary.setApprovedAt(LocalDateTime.now());
            salary.setIsActive(true);

            salaryBatch.add(salary);
        }

        salaryRecordRepository.saveAll(salaryBatch);
        log.info("Saved {} employees and corresponding active salary records.", savedEmployees.size());
    }
}
