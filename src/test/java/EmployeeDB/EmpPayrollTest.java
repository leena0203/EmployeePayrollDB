package EmployeeDB;

import static org.junit.Assert.*;

import java.time.LocalDate;
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
	public void givenNewEmployee_WhenAddedToPayroll_ShouldBeAddedToDepartment() {
		test.addEmployeeToPayrollAndDepartment("Peter",5000000.0, LocalDate.now(), "M", "Marketing");
		boolean result = test.checkEmployeePayrollInSyncWithDBI("Peter");
		assertEquals(true, result);
	}
}
