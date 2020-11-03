package EmployeeDB;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.Date;

public class EmployeeDB {
	private PreparedStatement employeePayrollDataStatement;
	private static EmployeeDB employeeDB;
	private EmployeeDB() {
	}
	public static EmployeeDB getInstance() {
		if (employeeDB == null)
			employeeDB = new EmployeeDB();
		return employeeDB;
	}
	private Connection getConnection() throws SQLException{
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "Sql@2020sql";
		Connection connection;
		System.out.println("Connecting to database:"+jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection is successful: " + connection);
		return connection;
	}


	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass = (Driver) driverList.nextElement();
			System.out.println("  " + driverClass.getClass().getName());
		}
	}


	public List<EmployeePayrollData> readData()  {
		String sql = "SELECT * FROM employee_payroll;";
		return this.getEmployeePayrollDataUsingDB(sql);
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		List<EmployeePayrollData> list = null;
		if(this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			list = this.getEmployeePayrollData(resultSet);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
		List<EmployeePayrollData> list = new ArrayList<>();
		try {
			while(result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate start =result.getDate("start").toLocalDate();
				list.add(new EmployeePayrollData(id, name, salary, start));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public int updateEmployeeData(String name, double salary) {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}
	private int updateEmployeeDataUsingStatement(String name, double salary) {
		String sql = String.format("update employee_payroll set salary = %.2f  where name = '%s';", salary, name);
		try(Connection connection  = this.getConnection()){
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
		try (Connection connection = this.getConnection()) {
			String sql = "Update employee_payroll set salary = ? where name = ? ; " ; 
			PreparedStatement prepareStatement = (PreparedStatement) connection.prepareStatement(sql);
			prepareStatement.setDouble(1, salary);
			prepareStatement.setString(2, name);
			return prepareStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * UC5_Update data between date range
	 * @param start
	 * @param end
	 * @return
	 */
	public List<EmployeePayrollData> getEmployeeForDateRange(LocalDate start, LocalDate end) {
		String sql = String.format("SELECT * FROM employee_payroll WHERE START BETWEEN '%s' AND '%s';",
				Date.valueOf(start), Date.valueOf(end));
		return this.getEmployeePayrollDataUsingDB(sql);
	}
	/**
	 * get Employee payroll data
	 * @param sql
	 * @return
	 */
	private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
		List<EmployeePayrollData> list = new ArrayList<>();
		try(Connection connection  = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			list = this.getEmployeePayrollData(result);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * Get Employee aggregate functions
	 * @param string 
	 * @return
	 */
	public Map<String, Double> getAggregateFunctions(String Function) {
		String sql = String.format("select gender, %s(salary) from employee_payroll group by gender;",Function);
		Map<String, Double> genderBasedFunctions = new HashMap<>();
		try(Connection connection  = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				String gender = resultSet.getString(1);
				double salary = resultSet.getDouble(2);
				genderBasedFunctions.put(gender, salary);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return genderBasedFunctions;
	}
	/**
	 * Insert new employee data into table
	 * @param name
	 * @param salary
	 * @param start
	 * @param gender
	 * @return
	 */
	public EmployeePayrollData addEmployeeToPayrollUC7(String name, double salary, LocalDate start, String gender) {
		int id = -1;
		EmployeePayrollData data = null;
		String sql = String.format("INSERT INTO employee_payroll (name, gender, salary,start)"+
				"VALUES( '%s', '%s', '%s', '%s')", name, gender, salary, Date.valueOf(start));
		try(Connection connection  = this.getConnection()){
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) id = resultSet.getInt(1);
			}
			data = new EmployeePayrollData(id, name, salary, start);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	/**
	 * Insert Employee
	 * @param name
	 * @param salary
	 * @param start
	 * @param gender
	 * @return
	 */
	public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate start, 
			String gender) {
		int id = -1;
		EmployeePayrollData data = null;
		Connection connection = null;
		try{
			connection  = this.getConnection();
			connection.setAutoCommit(false);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		try(Statement statement = connection.createStatement()){
			String sql = String.format("INSERT INTO employee_payroll (name, gender, salary,start)"+
					"VALUES( '%s', '%s', '%s', '%s')", name, gender, salary, Date.valueOf(start));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) id = resultSet.getInt(1);
			}
			data = new EmployeePayrollData(id, name, salary, start);
		}catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return data;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try(Statement statement = connection.createStatement()){
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format("INSERT INTO payroll_details (id, basic_pay, deductions,taxable_pay, tax, net_pay)VALUES"+
					"( '%s', '%s', '%s', '%s', '%s', '%s')", id, salary, deductions,taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if(rowAffected == 1) {
				data = new EmployeePayrollData(id, name, salary, start);
			}
		}catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return data;
	}

	public EmployeePayrollData addEmployeeToDepartment(String name, double salary, LocalDate start, String gender,
			String department) {
		int id = -1;
		EmployeePayrollData data = null;
		String sql = String.format("INSERT INTO employee_payroll (name, gender, salary,start, department)"+
				"VALUES( '%s', '%s', '%s', '%s', '%s')", name, gender, salary, Date.valueOf(start), department);
		try(Connection connection  = this.getConnection()){
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) id = resultSet.getInt(1);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
}




