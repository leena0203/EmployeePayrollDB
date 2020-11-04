package EmployeeDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}

	//List<EmployeePayrollData> employeePayrollList;
	List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
	private static EmployeeDB employeeDB;
	public EmployeePayrollService() {
		employeeDB = EmployeeDB.getInstance();
	}
	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}
	static Scanner consoleInputReader = new Scanner(System.in);
	public static void main(String[] args) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);

		employeePayrollService.readEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
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
	public void writeEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO))
			System.out.println("Writing Employee Payroll Roaster to console\n " +employeePayrollList);// TODO Auto-generated method stub
		else if(ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOservice().writeData(employeePayrollList);
	}
	/**
	 * UC2_Read employee_payroll data fom DB_IO File
	 * @param ioService
	 * @return
	 */
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
			this.employeePayrollList = employeeDB.readData();
		}
		return employeePayrollList;

	}
	/**
	 * UC3,UC4
	 * Update employee_payroll salary
	 * @param name
	 * @param salary
	 */
	public void updateEmployeeSalary(String name, double salary) {
		int result = employeeDB.updateEmployeeData(name, salary);	
		if (result ==0)return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null) employeePayrollData.salary = salary;
	}
	/**
	 * get employee_payroll data using stream
	 * @param name
	 * @return
	 */
	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
				.findFirst()
				.orElse(null);
	}
	public boolean checkEmployeePayrollInSyncWithDBI(String name) {
		List<EmployeePayrollData> list = employeeDB.getEmployeePayrollData(name);
		return list.get(0).equals(getEmployeePayrollData(name));
	}

	/**
	 * Read employee_payroll data for date range
	 * @param start
	 * @param end
	 * @return
	 */
	public List<EmployeePayrollData> readEmployeePayrollForDateRange(LocalDate start, LocalDate end) {
		return employeeDB.getEmployeeForDateRange(start, end);
	}
	/**
	 * Aggregate Functions
	 * @return
	 */
	public Map<String, Double> readAvgSalaryByGender() {
		return employeeDB.getAggregateFunctions("AVG");
	}
	public Map<String, Double> readMinSalaryByGender() {
		return employeeDB.getAggregateFunctions("MIN");
	}
	public Map<String, Double> readMaxSalaryByGender() {
		return employeeDB.getAggregateFunctions("MAX");
	}
	public Map<String, Double> readSumByGender() {
		return employeeDB.getAggregateFunctions("SUM");
	}
	public Map<String, Double> readCountByGender() {
		return employeeDB.getAggregateFunctions("COUNT");
	}
	/**
	 * Add new employee to table
	 * @param name
	 * @param salary
	 * @param start
	 * @param gender
	 */
	public void addEmployeeToPayroll(String name, double salary, LocalDate start, String gender) {
		employeePayrollList.add(employeeDB.addEmployeeToPayrollUC7(name, salary, start, gender));
	}
	/**
	 * add employee to department
	 * @param name
	 * @param salary
	 * @param start
	 * @param gender
	 * @param department
	 */
	public void addEmployeeToDepartment(String name, double salary, LocalDate start, String gender, String department) {
		employeePayrollList.add(employeeDB.addEmployeeToDepartment(name, salary, start, gender, department));
	}
	public List<EmployeePayrollData> deleteEmployee(String name) {
		employeeDB.deleteEmployee(name);
		return readEmployeePayrollData(IOService.DB_IO);
		
	}
	/**
	 * add employee to department and payroll
	 * @param name
	 * @param salary
	 * @param start
	 * @param gender
	 * @param department
	 */
	public void addEmployeeToPayrollAndDepartment(String name, double salary, LocalDate start, String gender,
			String department) {
		this.employeePayrollList.add(employeeDB.addEmployeeToPayrollAndDepartment(name,salary, start,department, gender));
		
	}
	/**
	 * Remove employee from payroll
	 * @param id
	 * @return
	 */
	public List<EmployeePayrollData> removeEmployeeFromPayroll(int id) {
		List<EmployeePayrollData> activeList = null;
		activeList = employeeDB.removeEmployeeFromPayroll(id);
		return activeList;
	}
}
