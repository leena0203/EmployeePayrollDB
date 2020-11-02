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
	    test.updateEmployeeSalary("Terisa", 3000000.00);
	    boolean result = test.checkEmployeePayrollInSyncWithDBI("Terisa");
		assertTrue(result);
	}
	//UC5
	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
	    LocalDate start = LocalDate.of(2020, 8, 01);
	    LocalDate end = LocalDate.now();
	    testData = test.readEmployeePayrollForDateRange(start, end);
	    assertEquals(2, testData.size());
	}
	//UC6
	@Test
	public void givenPayrollData_WhenAvgSalaryRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readAvgSalaryByGender();
	    assertTrue(genderBasedFunctions.get("M").equals(3650000.0));
	    assertTrue(genderBasedFunctions.get("F").equals(3000000.0));
	}
	@Test
	public void givenPayrollData_WhenMinSalaryRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readMinSalaryByGender();
	    assertTrue(genderBasedFunctions.get("M").equals(2300000.0));
	    assertTrue(genderBasedFunctions.get("F").equals(3000000.0));
	}
	@Test
	public void givenPayrollData_WhenMaxSalaryRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readMaxSalaryByGender();
	    assertTrue(genderBasedFunctions.get("M").equals(5000000.0));
	    assertTrue(genderBasedFunctions.get("F").equals(3000000.0));
	}
	@Test
	public void givenPayrollData_WhenSumRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readSumByGender();
	    assertTrue(genderBasedFunctions.get("M").equals(7300000.0));
	    assertTrue(genderBasedFunctions.get("F").equals(3000000.0));
	}
	@Test
	public void givenPayrollData_WhenCountRetrievedByGender_ShouldReturnProperValue() {
	    Map<String, Double> genderBasedFunctions = test.readCountByGender();
	    assertTrue(genderBasedFunctions.get("M").equals(2.0));
	    assertTrue(genderBasedFunctions.get("F").equals(1.0));
	}
}
