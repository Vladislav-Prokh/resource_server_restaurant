package delivery.app.controllers.employees;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import delivery.app.entities.Employee;
import delivery.app.entities.Role;
import delivery.app.exceptions.ResourceNotFoundException;
import delivery.app.repositories.EmployeeRepository;
import delivery.app.services.EmployeeService;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(roles = "ADMIN")
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc; 
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private EmployeeService employeeService; 
    @Test
    public void testFindById_Success() throws Exception {
       
        Employee mockEmployee = new Employee(1L, "John", "Doe", "email@gmail.com", Role.ADMIN);

     
        when(employeeService.findEmployeeById(1L)).thenReturn(mockEmployee);

        mockMvc.perform(get("/employees/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.employeeName").value("John"))
               .andExpect(jsonPath("$.role").value("ADMIN"));
    }
    @Test
    public void testFindAllEmployees_Success() throws Exception {
   
        Employee employee1 = new Employee(1L, "John", "Doe", "john.doe@example.com", Role.ADMIN);
        Employee employee2 = new Employee(2L, "Jane", "Doe", "jane.doe@example.com", Role.WAITER);
        
 
        Page<Employee> employeesPage = new PageImpl<>(List.of(employee1, employee2), PageRequest.of(0, 10), 2);
        
        when(employeeService.getEmployees(0, 10)).thenReturn(employeesPage);

        
        mockMvc.perform(get("/employees")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.content.length()").value(2)) 
                .andExpect(jsonPath("$.content[0].employeeName").value("John")) 
                .andExpect(jsonPath("$.content[1].employeeName").value("Jane")) 
                .andExpect(jsonPath("$.content[0].role").value("ADMIN")) 
                .andExpect(jsonPath("$.content[1].role").value("WAITER")); 
    }
    @Test
    public void testAssignRole_Success() throws Exception {
        Long employeeId = 1L;

        doNothing().when(employeeService).assignRole(eq(employeeId), eq("ADMIN"));

        mockMvc.perform(post("/employees/{employeeId}/role", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ADMIN\"}"))
               .andExpect(status().isOk())
               .andExpect(content().string("Role assigned successfully"));
    }

    @Test
    public void testAssignRole_Failure() throws Exception {
        Long employeeId = 1L;

        doThrow(new RuntimeException("Failed to assign role")).when(employeeService).assignRole(eq(employeeId), eq("ADMIN"));

        mockMvc.perform(post("/employees/{employeeId}/role", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ADMIN\"}"))
               .andExpect(status().isInternalServerError())
               .andExpect(content().string("Failed to assign role"));
    }
    
    @Test
    public void testDeleteEmployeeById_Success() throws Exception {
     
        Long employeeId = 1L;

        doNothing().when(employeeService).deleteEmployeById(employeeId);

        mockMvc.perform(delete("/employees/{employee-id}", employeeId))
               .andExpect(status().isNoContent()); 
    }

    @Test
    public void testDeleteEmployeeById_Fail_EmployeeNotFound() throws Exception {
        Long employeeId = 1L;

        doThrow(new ResourceNotFoundException("Employee not found")).when(employeeService).deleteEmployeById(employeeId);

        mockMvc.perform(delete("/employees/{employee-id}", employeeId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value("Employee not found"));
    }
}
