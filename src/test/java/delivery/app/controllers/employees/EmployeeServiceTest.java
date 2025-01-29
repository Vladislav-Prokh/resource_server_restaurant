package delivery.app.controllers.employees;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import delivery.app.entities.Employee;
import delivery.app.entities.Role;
import delivery.app.repositories.EmployeeRepository;
import delivery.app.services.EmployeeService;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(roles = "ADMIN")
public class EmployeeServiceTest {


	    @Autowired
	    private EmployeeService employeeService; 

	    @MockBean
	    private EmployeeRepository employeeRepository;
	    @Test
	    public void testFindByEmployeeEmail_Success() {
	
	        String email = "test@example.com";
	        Employee mockEmployee = new Employee(1L, "John", "Doe", email, Role.ADMIN);

	        when(employeeRepository.findByEmployeeEmail(email)).thenReturn(mockEmployee);

	        Employee result = employeeService.findByEmployeeEmail(email);

	        assertNotNull(result);
	        assertEquals(mockEmployee.getEmployeeEmail(), result.getEmployeeEmail());
	    }

	    @Test
	    public void testFindByEmployeeEmail_EmployeeNotFound() {
	        String email = "nonexistent@example.com";

	        when(employeeRepository.findByEmployeeEmail(email)).thenReturn(null);

	        Employee result = employeeService.findByEmployeeEmail(email);

	        assertNull(result);
	    }
	    
	    
}
