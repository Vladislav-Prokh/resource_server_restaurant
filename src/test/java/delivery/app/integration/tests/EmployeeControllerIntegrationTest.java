package delivery.app.integration.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import delivery.app.dto.RoleRequest;
import delivery.app.entities.Employee;
import delivery.app.entities.Role;
import delivery.app.repositories.EmployeeRepository;
import jakarta.transaction.Transactional;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
public class EmployeeControllerIntegrationTest{
	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
	private EmployeeRepository employeeRepository;
    
    @Test
    public void testFindEmployeeById() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeEmail("test_empl@gmail.com");
        employee.setEmployeeName("test");
        employee.setEmployeeSurName("test too");
        employee.setRole(Role.ADMIN);
        employee = employeeRepository.save(employee);

        mockMvc.perform(get("/employees/" + employee.getEmployeeId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(employee.getEmployeeId()))
                .andExpect(jsonPath("$.employeeName").value("test"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    public void testFindAllEmployees() throws Exception {
  
        Employee employee1 = new Employee();
        employee1.setEmployeeEmail("test_emp2l@gmail.com");
        employee1.setEmployeeName("empl1");
        employee1.setEmployeeSurName("user");
        employee1.setRole(Role.ADMIN);
        Employee employee2 = new Employee();
        employee2.setEmployeeEmail("test_emp1l@gmail.com");
        employee2.setEmployeeName("empl2");
        employee2.setEmployeeSurName("user");
        employee2.setRole(Role.ADMIN);
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        mockMvc.perform(get("/employees?page=0&size=4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].employeeName").value("empl1"))
                .andExpect(jsonPath("$.content[1].employeeName").value("empl2"));
    }

    @Test
    public void testAssignRoleToEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeEmail("test_emp2l@gmail.com");
        employee.setEmployeeName("empl1");
        employee.setEmployeeSurName("user");
        employee.setRole(Role.ADMIN);
        employee = employeeRepository.save(employee);

        RoleRequest roleRequest = new RoleRequest("Admin");

        mockMvc.perform(post("/employees/" + employee.getEmployeeId() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Role assigned successfully"));

        Employee updatedEmployee = employeeRepository.findById(employee.getEmployeeId()).orElse(null);
        assertNotNull(updatedEmployee);
        assertEquals("ADMIN", updatedEmployee.getRole().toString());
    }

    @Test
    public void testDeleteEmployeeById() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeEmail("test_emp2l@gmail.com");
        employee.setEmployeeName("empl1");
        employee.setEmployeeSurName("user");
        employee.setRole(Role.ADMIN);
        
        employee = employeeRepository.save(employee);

        mockMvc.perform(delete("/employees/" + employee.getEmployeeId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(employeeRepository.findById(employee.getEmployeeId()).isPresent());
    }
}
