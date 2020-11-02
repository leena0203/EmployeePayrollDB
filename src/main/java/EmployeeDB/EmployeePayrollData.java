package EmployeeDB;

import java.time.LocalDate;

public class EmployeePayrollData {
	public int id;
	public String name;
	public double salary;
	public LocalDate start;
	public EmployeePayrollData(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}
	public EmployeePayrollData(int id, String name, double salary, LocalDate start) {
		this(id, name, salary);
		this.start = start;
	}
	public String toString(){
		return " Id: "+id+ " Name: "+name+ " Salary:" +salary;
	}
}
