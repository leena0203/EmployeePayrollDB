package EmployeeDB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollFileIOservice {
	public static String PAYROLL_FILE_NAME = "payroll-file.txt";

	public void writeData(List<EmployeePayrollData> employeePayrollList) {
		StringBuffer empBuffer =  new StringBuffer();
		employeePayrollList.forEach(employee -> {
			String employeeString = employee.toString().concat("\n");
			empBuffer.append(employeeString);
		});
		try {
			Files.write(Paths.get(PAYROLL_FILE_NAME), empBuffer.toString().getBytes());
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * print the data
	 */
	public void printData() {
		try {
			Files.lines(new File("payroll-file.txt").toPath()).forEach(System.out::println);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * count the input entries
	 * @return
	 */
	public long countEntries() {
		long entries = 0;
		try {
			entries = Files.lines(new File("payroll-file.txt").toPath()).count();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return entries;
	}
	
	/**
	 * Read Employee payroll data
	 */
	public List<EmployeePayrollData> readData(){
		List<EmployeePayrollData> list = new ArrayList<EmployeePayrollData>();
		try{
			Files.lines(new File(PAYROLL_FILE_NAME).toPath()).map(line ->line.trim()).forEach(line ->
			{
				String[] data = line.split("(, )");
				String[] newData = new String[10];
				int index = 0;
				for(String d : data) {
					String[] splitData = d.split("(=)");
					newData[index] = splitData[1];
					index++;
				}
				list.add(new EmployeePayrollData(Integer.parseInt(newData[0]), newData[1],
						Double.parseDouble(newData[2])));
			});
		}catch(IOException e) {
			e.printStackTrace();
		}return list;
	}

}
