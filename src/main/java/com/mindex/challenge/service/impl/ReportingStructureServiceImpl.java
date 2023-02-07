package com.mindex.challenge.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

	private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);
	private static final String DATASTORE_LOCATION = "/static/employee_database.json";

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public ReportingStructure getDetails(String id) {
		LOG.info("Employee Id :{} ",id);
		ReportingStructure report = new ReportingStructure();
		InputStream inputStream = this.getClass().getResourceAsStream(DATASTORE_LOCATION);
		Employee[] employees = null;
		try {
			employees = objectMapper.readValue(inputStream, Employee[].class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		constructResponse(id, employees, report);
		return report;
	}

	private void constructResponse(String id, Employee[] employees, ReportingStructure report) {
		HashMap<String, Employee> empMap = new HashMap<>();
		for (Employee employee : employees) {
			if (null != employee) {
				empMap.put(employee.getEmployeeId(), employee);
			}
		}
		if (empMap.containsKey(id)) {
			boolean parentEmp = true;
			List<Employee> numberOfReports = new ArrayList<Employee>();
			Employee employee = empMap.get(id);
			Employee employee3 = getEmployee(employee, parentEmp, numberOfReports);

			if (null != employee.getDirectReports()) {
				parentEmp = false;
				List<Employee> directReports = employee.getDirectReports();
				getDiretReporters(empMap, parentEmp, directReports, numberOfReports);
			}
			report.setEmployee(employee3);
			report.setNumberOfReports(numberOfReports);
		}
	}

	private void getDiretReporters(HashMap<String, Employee> empMap, boolean parentEmp, List<Employee> directReports,
			List<Employee> numberOfReports) {
		if (null != directReports && !ObjectUtils.isEmpty(directReports)) {
			for (Employee employee2 : directReports) {
				String employeeId = employee2.getEmployeeId();
				Employee employee = empMap.get(employeeId);
				getEmployee(employee, parentEmp, numberOfReports);
				if (null != employee) {
					List<Employee> directReports2 = employee.getDirectReports();
					getDiretReporters(empMap, parentEmp, directReports2, numberOfReports);
				}
			}
		}
	}

	private Employee getEmployee(Employee employee, boolean parentEmp, List<Employee> numberOfReports) {
		Employee emp = new Employee();
		emp.setEmployeeId(employee.getEmployeeId());
		emp.setDepartment(employee.getDepartment());
		emp.setFirstName(employee.getFirstName());
		emp.setLastName(employee.getLastName());
		emp.setPosition(employee.getPosition());
		if (!parentEmp) {
			numberOfReports.add(emp);
		}
		return emp;
	}

}
