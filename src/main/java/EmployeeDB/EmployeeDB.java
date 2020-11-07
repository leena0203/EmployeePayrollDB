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
	private int connectionCounter = 0;
	private PreparedStatement employeePayrollDataStatement;
	private static EmployeeDB employeeDB;
	private EmployeeDB() {
	}
	public static EmployeeDB getInstance() {
		if (employeeDB == null)
			employeeDB = new EmployeeDB();
		return employeeDB;
	}
		private synchronized Connection getConnection() throws SQLException {
			connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "Sql@2020sql";
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Processing Thread: "+Thread.currentThread().getName()+
                               " Connecting to database with Id: "+connectionCounter);
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			System.out.println("Processing Thread: "+Thread.currentThread().getName()+
                               " Connecting to database with Id: "+connectionCounter+" Connection is successfull!!"+connection);
		} catch (Exception e) {
			throw new SQLException("Connection was unsuccessful");
		}
		return connection;
		}


	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass = (Driver) driverList.nextElement();
			System.out.println("  " + driverClass.getClass().getName());
		}
	}
	/**
	 * Employee payroll data
	 * @param result
	 * @return
	 */
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
	/**
	 * get Employee payroll data Using DB
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
	 * UC2,UC10: Read employee data
	 * @return
	 */
	public List<EmployeePayrollData> readData() {
		String sql = "Select employee_payroll.id,name,gender,salary,start,department.department from employee_payroll "
				+ "inner join department on employee_payroll.id = department.id; ";
		return this.getEmployeePayrollAndDeparmentData(sql);
	}
	/**
	 * Update employee payroll data
	 * @param name
	 * @param salary
	 * @return
	 */
	public int updateEmployeeData(String name, double salary) {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}
	/**
	 * update using statement
	 * @param name
	 * @param salary
	 * @return
	 */
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

	/**
	 * get data to sync with DB
	 * @param name
	 * @return
	 */
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

	/**
	 * prepared statement
	 */
	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * update data using prepared statement
	 * @param name
	 * @param salary
	 * @return
	 */
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
		String sql = String.format("Select emplpyee_payroll.id,name,gender,salary,start,department.department from employee_payroll "
				+ "inner join department on employee_payroll.id = department.id where start between '%s' and '%s' ;",
				Date.valueOf(start), Date.valueOf(end));
		return this.getEmployeePayrollAndDeparmentData(sql);
	}
	/**
	 * UC10_New Table
	 * @param sql
	 * @return
	 */
	private List<EmployeePayrollData> getEmployeePayrollAndDeparmentData(String sql) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				String department = resultSet.getString("department");
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, start,department));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;		
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
	public EmployeePayrollData addEmployeeToPayrollAndDepartment(String name, double salary, LocalDate start, 
			String gender, List<String> department) {
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
		}catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO department (id, department) "
							+ "VALUES ('%s','%s','%s')",
					id,department);
			int rowAffected = statement.executeUpdate(sql);
			if(rowAffected == 1) {
				data = new EmployeePayrollData(id,name, salary,gender,start, department);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException exception) {
				exception.printStackTrace();
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
	/**UC8
	 * dELETE EMPLOYEE(CASCADING)
	 * @param name
	 * @throws DatabaseException
	 */
	public void deleteEmployee(String name) {
		String sql = String.format("DELETE from employee_payroll_service where name = '%s';", name);
		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * UC9
	 * add employee to department
	 * @param name
	 * @param salary
	 * @param start
	 * @param gender
	 * @param department
	 * @return
	 */
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
			data = new EmployeePayrollData(id, name, salary, start);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	/**
	 * Remove employee from payroll
	 * @param id
	 * @return
	 */
	public List<EmployeePayrollData> removeEmployeeFromPayroll(int id) {
		List<EmployeePayrollData> listOfEmployees = this.readData();
		listOfEmployees.forEach(employee -> {
			if (employee.id == id) {
				employee.is_active = false;
			}
		});
		return listOfEmployees;
	}
	
	
}




