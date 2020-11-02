package EmployeeDB;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import EmployeeDB.EmployeePayrollService.IOService;

public class EmpPayrollTest {
	public static EmployeePayrollService test;
	public static List<EmployeePayrollData> testData;
	//UC2
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		test = new EmployeePayrollService();
	    testData = test.readEmployeePayrollData(IOService.DB_IO);
		assertEquals(3, testData.size());
	}
	//UC3 and UC4
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncDatabase() {
		test = new EmployeePayrollService();
	    testData = test.readEmployeePayrollData(IOService.DB_IO);
	    test.updateEmployeeSalary("Terisa", 3000000.00);
	    boolean result = test.checkEmployeePayrollInSyncWithDBI("Terisa");
		assertTrue(result);
	}
	//UC5
	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		test = new EmployeePayrollService();
		test.readEmployeePayrollData(IOService.DB_IO);
	    LocalDate start = LocalDate.of(2020, 8, 01);
	    LocalDate end = LocalDate.now();
	    testData = test.readEmployeePayrollForDateRange(start, end);
	    assertEquals(2, testData.size());
	}
}
