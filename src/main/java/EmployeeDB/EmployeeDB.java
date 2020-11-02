package EmployeeDB;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.sql.Connection;

public class EmployeeDB {
	private Connection getConnection() throws SQLException{
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "Sql@2020sql";
		Connection connection;
		//		try {
		//			Class.forName("com.mysql.jdbc.Driver");
		//			System.out.println("Driver loaded");
		//		}catch (ClassNotFoundException e) {
		//			throw new IllegalStateException("Cannot find driver", e);
		//		}
		//		listDrivers();
		//		try {
		System.out.println("Connecting to database:"+jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection is successful: " + connection);
		//		} catch (SQLException exception) {
		//			exception.printStackTrace();
		//		}
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
			while(result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary  = result.getDouble("salary");
				LocalDate start = result.getDate("start").toLocalDate();
				list.add(new EmployeePayrollData(id, name, salary, start));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

}
