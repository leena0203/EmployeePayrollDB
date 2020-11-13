package EmployeeDB;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmployeePayrollService {
	private static final Logger LOG = LogManager.getLogger(EmployeeDB.class); 
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	// List<EmployeePayrollData> employeePayrollList;
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
			System.out.println("Writing Employee Payroll Roaster to console\n " + employeePayrollList);// TODO
		// Auto-generated
		// method stub
		else if (ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOservice().writeData(employeePayrollList);
	}

	/**
	 * UC2_Read employee_payroll data fom DB_IO File
	 * 
	 * @param ioService
	 * @return
	 */
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO)) {
			System.out.println("Enter Employee Id: ");
			int id = consoleInputReader.nextInt();
			System.out.println("Enter Employee Name: ");
			String name = consoleInputReader.next();
			System.out.println("Enter Employee Salary: ");
			double salary = consoleInputReader.nextDouble();
			employeePayrollList.add(new EmployeePayrollData(id, name, salary));
		} else if (ioService.equals(IOService.FILE_IO)) {
			System.out.println("reading data from file.");
			new EmployeePayrollFileIOservice().printData();
		} else if (ioService.equals(IOService.DB_IO)) {
			this.employeePayrollList = employeeDB.readData();
		}
		return employeePayrollList;

	}

	/**
	 * UC3,UC4 Update employee_payroll salary
	 * 
	 * @param name
	 * @param salary
	 */
	public void updateEmployeeSalary(String name, double salary) {
		int result = employeeDB.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.salary = salary;
	}

	/**
	 * get employee_payroll data using stream
	 * 
	 * @param name
	 * @return
	 */
	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name)).findFirst().orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDBI(String name) {
		List<EmployeePayrollData> list = employeeDB.getEmployeePayrollData(name);
		return list.get(0).equals(getEmployeePayrollData(name));
	}

	/**
	 * Read employee_payroll data for date range
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<EmployeePayrollData> readEmployeePayrollForDateRange(LocalDate start, LocalDate end) {
		return employeeDB.getEmployeeForDateRange(start, end);
	}

	/**
	 * Aggregate Functions
	 * 
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
	 * 
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
	 * 
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
	 * 
	 * @param name
	 * @param salary
	 * @param start
	 * @param department2
	 * @param department
	 */
	public void addEmployeeToPayrollAndDepartment(String name, double salary, LocalDate start, String gender,
			List<String> department) throws SQLException {
		this.employeePayrollList
		.add(employeeDB.addEmployeeToPayrollAndDepartment(name, salary, start, gender, department));

	}

	/**
	 * Remove employee from payroll
	 * 
	 * @param id
	 * @return
	 */
	public List<EmployeePayrollData> removeEmployeeFromPayroll(int id) {
		List<EmployeePayrollData> activeList = null;
		activeList = employeeDB.removeEmployeeFromPayroll(id);
		return activeList;
	}

	/**
	 * ADD Multiple contacts without thread
	 * 
	 * @param employeeList
	 */
	public void addEmployeesToPayroll(List<EmployeePayrollData> employeeList) {
		employeeList.forEach(employee -> {
			// System.out.println("Employee Being added: "+employee.name);
			try {
				this.addEmployeeToPayrollAndDepartment(employee.name, employee.salary, employee.start, employee.gender,
						employee.department);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// System.out.println("Employee added: "+employee.name);
		});
		// System.out.println(this.employeeList);
	}

	/**
	 * add multiple contacts using threads
	 * 
	 * @param employeeList
	 */
	public void addEmployeesToPayrollWithThreads(List<EmployeePayrollData> employeeList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
		employeeList.forEach(employee -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(employee.hashCode(), false);
				System.out.println("Employee Being Added: " + Thread.currentThread().getName());
				try {
					this.addEmployeeToPayrollAndDepartment(employee.name, employee.salary, employee.start,
							employee.gender, employee.department);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				employeeAdditionStatus.put(employee.hashCode(), true);
				System.out.println("Employee Added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, employee.name);
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void updatePayroll(Map<String, Double> salaryMap) throws SQLException{
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
		salaryMap.forEach((k, v) -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(k.hashCode(), false);
				LOG.info("Employee Being Added: " + Thread.currentThread().getName());
				this.updatePayrollDB(k, v);
				employeeAdditionStatus.put(k.hashCode(), true);
				LOG.info("Employee Added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, k);
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void updatePayrollDB(String name, Double salary) {
		int result = employeeDB.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employee = this.getEmployeePayrollData(name);
		if (employee != null)
			employee.salary = salary;
	}

	public boolean checkEmployeeListSync(List<String> asList) throws SQLException {
		List<Boolean> resultList = new ArrayList<>();
		asList.forEach(name -> {
			List<EmployeePayrollData> employeeList;
			employeeList = employeeDB.getEmployeePayrollData(name);
			resultList.add(employeeList.get(0).equals(getEmployeePayrollData(name)));
		});
		if(resultList.contains(false)){
			return false;
		}
		return true;
	}

	public void addEmployeesToPayroll(EmployeePayrollData employee) {
		employeePayrollList.add(employee);		
	}

	
}
