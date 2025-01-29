package delivery.app.services;

import delivery.app.exceptions.InvalidRoleException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import delivery.app.entities.Employee;
import delivery.app.entities.Role;
import delivery.app.exceptions.ResourceNotFoundException;
import delivery.app.repositories.EmployeeRepository;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	public Employee saveEmployee(Employee employee) {
		return this.employeeRepository.save(employee);
	}

	public Employee findEmployeeById(Long employeeId){
		return this.employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("employee not found"));
	}

	public Employee findByEmployeeEmail(String email) {
		return this.employeeRepository.findByEmployeeEmail(email);
	}
	

	public Page<Employee> getEmployees(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return employeeRepository.findAll(pageable);
	}

	public void deleteEmployeById(Long employee_id) {
		this.employeeRepository.deleteById(employee_id);
	}

	public void assignRole(Long employeeId, String role) throws InvalidRoleException {

	    Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new InvalidRoleException("employee not found"));
        Role newRole = Role.valueOf(role.toUpperCase());
        employee.setRole(newRole);
        this.employeeRepository.save(employee);

	}
}
