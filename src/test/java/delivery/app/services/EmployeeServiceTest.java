package delivery.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import delivery.app.exceptions.InvalidRoleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import delivery.app.entities.Employee;
import delivery.app.entities.Role;
import delivery.app.repositories.EmployeeRepository;

public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee(1L, "John", "Doe", "developer", Role.ADMIN);
    }

    @Test
    public void testSaveEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee savedEmployee = employeeService.saveEmployee(employee);
        assertNotNull(savedEmployee);
        assertEquals(employee.getEmployeeId(), savedEmployee.getEmployeeId());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void testFindEmployeeById()  {
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        Employee foundEmployee = employeeService.findEmployeeById(1L);

        assertNotNull(foundEmployee);
        assertEquals(employee.getEmployeeId(), foundEmployee.getEmployeeId());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee));
        when(employeeRepository.findAll(pageable)).thenReturn(page);

        Page<Employee> employees = employeeService.getEmployees(0, 10);

        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        verify(employeeRepository, times(1)).findAll(pageable);
    }
    
    @Test
    public void testAssignRoleThrowsExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidRoleException.class, () -> employeeService.assignRole(1L, "admin"));
    }


    @Test
    public void testDeleteEmployeeById() {
        employeeService.deleteEmployeById(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testAssignRole() throws InvalidRoleException {
 
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.assignRole(1L, "admin");

        assertEquals(Role.ADMIN, employee.getRole());
        
        verify(employeeRepository, times(1)).save(employee); 
    }


}
