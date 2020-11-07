package EmployeeDB;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import EmployeeDB.EmployeePayrollService.IOService;

public class EmpPayrollTest {
	public static EmployeePayrollService test;
	public static List<EmployeePayrollData> testData;
	@Before
	public void setUp() {
		test = new EmployeePayrollService();
	    testData = test.readEmployeePayrollData(IOService.DB_IO);
	}
	//UC2
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		assertEquals(3, testData.size());
	}

	//UC3 and UC4
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncDatabase() {
	    test.updateEmployeeSalary("Leena", 3000000.00);
	    boolean result = test.checkEmployeePayrollInSyncWithDBI("Leena");
		assertTrue(result);
	}

	//UC5
	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
	    LocalDate start = LocalDate.of(2019, 9, 01);
	    LocalDate end = LocalDate.now();
	    testData = test.readEmployeePayrollForDateRange(start, end);
	    assertEquals(2, testData.size());
	}
	//UC6
	@Test
	public void givenPayrollData_WhenAvgSalaryRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readAvgSalaryByGender();
	    assertTrue(genderBasedFunctions.get("M").equals(5400000.0));
	    assertTrue(genderBasedFunctions.get("F").equals(3000000.0));
	}
	@Test
	public void givenPayrollData_WhenMinSalaryRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readMinSalaryByGender();
	    assertTrue(genderBasedFunctions.get("M").equals(1200000.0));
	    assertTrue(genderBasedFunctions.get("F").equals(3000000.0));
	}
	@Test
	public void givenPayrollData_WhenMaxSalaryRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readMaxSalaryByGender();
	    assertTrue(genderBasedFunctions.get("M").equals(9000000.0));
	    assertTrue(genderBasedFunctions.get("F").equals(3000000.0));
	}
	@Test
	public void givenPayrollData_WhenSumRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readSumByGender();
	    assertTrue(genderBasedFunctions.get("F").equals( 3000000.0));
	    assertTrue(genderBasedFunctions.get("M").equals(10200000.0)); 
	    }
	@Test
	public void givenPayrollData_WhenCountRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readCountByGender();
	    assertTrue(genderBasedFunctions.get("M").equals(2.0));
	    assertTrue(genderBasedFunctions.get("F").equals(1.0));
	}
	
	//UC7
	@Test
	public void givenPayrollData_WhenAddedNewEntry_ShouldSyncWithDB() {
	    test.addEmployeeToPayroll("Mill", 6000000.00, LocalDate.now(), "M");
	    boolean result = test.checkEmployeePayrollInSyncWithDBI("Mill");
	    assertTrue(result);
	    System.out.println();
	}
	//UC8
	@Test
	public void givenEmployeePayrollInDB_WhenEmployeeDeleted_ShouldMatchEmployeeCount() {
		test.deleteEmployee("Mill");
		assertEquals(6, testData.size());
	}
	
	//UC9
	//UC11
	@Test
	public void givenNewEmployee_WhenAddedToPayroll_ShouldBeAddedToDepartment() throws SQLException {
		test.addEmployeeToPayrollAndDepartment("Peter",5000000.0, LocalDate.now(), "M",Arrays.asList("Marketing"));
		boolean result = test.checkEmployeePayrollInSyncWithDBI("Peter");
		assertEquals(true, result);
	}
	//UC12
	@Test
	public void givenEmployeeID_WhenRemoved_ShouldMatchEmployeeCountForActiveMember() {
		List<EmployeePayrollData> onlyActiveList = test.removeEmployeeFromPayroll(3);
		assertEquals(3, onlyActiveList.size());
	}
	//UC13 , UC14, UC15
	@Test
	public void geiven6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries()  {
		EmployeePayrollData[] arrayOfEmp = { new EmployeePayrollData(0, "Jeff Bezos", 100000.0, "M", LocalDate.now(), Arrays.asList("Sales")),
				new EmployeePayrollData(0, "Bill Gates", 200000.0, "M", LocalDate.now(), Arrays.asList("Marketing")),
				new EmployeePayrollData(0, "Mark ", 150000.0, "M", LocalDate.now(), Arrays.asList("Technical")),
				new EmployeePayrollData(0, "Sundar", 400000.0, "M", LocalDate.now(), Arrays.asList("Sales,Technical")),
				new EmployeePayrollData(0, "Mukesh ", 4500000.0, "M", LocalDate.now(), Arrays.asList("Sales")),
				new EmployeePayrollData(0, "Anil", 300000.0, "M", LocalDate.now(), Arrays.asList("Sales")) };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmp));
		Instant end = Instant.now();
		System.out.println("Duration without Thread: " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmp));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with Thread: " + Duration.between(threadStart, threadEnd));
		long result = employeePayrollService.countEntries(IOService.DB_IO);
		assertEquals(13, result);
	}
	//UC17
	@Test
	public void geiven2Employees_WhenUpdatedSalary_ShouldSyncWithDB() throws SQLException {
		Map<String, Double> salaryMap = new HashMap<>();
		salaryMap.put("Bill Gates",700000.0);
		salaryMap.put("Mukesh",800000.0);
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.updatePayroll(salaryMap);
		Instant end = Instant.now();
		System.out.println("Duration with Thread: " + Duration.between(start, end));
		boolean result = employeePayrollService.checkEmployeeListSync(Arrays.asList("Bill Gates,Mukesh"));
		assertEquals(true,result);
	}
}
