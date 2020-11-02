package EmployeeDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}

	List<EmployeePayrollData> employeePayrollList;

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}
	public EmployeePayrollService() {}
	static Scanner consoleInputReader = new Scanner(System.in);
	public static void main(String[] args) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);

		employeePayrollService.readEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
	}
	public void writeEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO))
			System.out.println("Writing Employee Payroll Roaster to console\n " +employeePayrollList);// TODO Auto-generated method stub
		else if(ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOservice().writeData(employeePayrollList);
	}
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.CONSOLE_IO)) {
			System.out.println("Enter Employee Id: ");
			int id = consoleInputReader.nextInt();
			System.out.println("Enter Employee Name: ");
			String name = consoleInputReader.next();
			System.out.println("Enter Employee Salary: ");
			double salary = consoleInputReader.nextDouble();
			employeePayrollList.add(new EmployeePayrollData(id, name, salary));
		}else if(ioService.equals(IOService.FILE_IO)) {
			System.out.println("reading data from file.");
			new EmployeePayrollFileIOservice().printData();
		}else if (ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = new EmployeeDB().readData();
		}
		return employeePayrollList;
		
	}
	public long countEntries(IOService fileIo) {
		long entries = 0;
		if (fileIo.equals(IOService.FILE_IO)) {
			entries = new EmployeePayrollFileIOservice().countEntries();
		}
		return entries;
	}
	public void printData(IOService fileIo) {
		if (fileIo.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOservice().printData();
		}
	}

}
