package EmployeeDB;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import java.sql.Connection;

public class DBDemo {
	public static void main(String[] args) {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "Sql@2020sql";
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded");
		}catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find driver", e);
		}
		listDrivers();
		try {
			System.out.println("Connecting to database:"+jdbcURL);
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			System.out.println("Connection is successful: " + connection);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}


	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass = (Driver) driverList.nextElement();
			System.out.println("  " + driverClass.getClass().getName());
		}
	}

}
