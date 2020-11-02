package EmployeeDB;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import EmployeeDB.EmployeePayrollService.IOService;

public class EmpPayrollTest {
	public static EmployeePayrollService test;
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		test = new EmployeePayrollService();
		List<EmployeePayrollData> testData = test.readEmployeePayrollData(IOService.DB_IO);
		assertEquals(3, testData.size());
	}

}
