package com.capgemini.employeeJDBC;

import java.time.LocalDate;
import java.util.List;

public class EmployeePayrollData {
	public int id;
	public String name;
	public double salary;
	public LocalDate start;
	public List<String> department;
	String gender;
	public boolean is_active;
	public EmployeePayrollData(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}
	public EmployeePayrollData(int id, String name, double salary, LocalDate start) {
		this(id, name, salary);
		this.start = start;
	}
	public EmployeePayrollData(int id, String name, double salary, LocalDate start, String gender) {
		this(id, name, salary, start);
		this.gender = gender;
	}
	
	public EmployeePayrollData(int id, String name, double salary, String gender, LocalDate start, List<String> department) {
		this(id, name, salary, start, gender);
		this.department= department;
	}
	
	public String toString(){
		return " Id: "+id+ " Name: "+name+ " Salary:" +salary;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(salary);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		return true;
	}
}
