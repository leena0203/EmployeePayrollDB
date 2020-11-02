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
import java.util.List;
import java.sql.Connection;

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



}
