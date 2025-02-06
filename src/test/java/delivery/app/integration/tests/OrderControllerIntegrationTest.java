package delivery.app.integration.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import delivery.app.dto.OrderRequestDTO;
import delivery.app.dto.OrderResponseDTO;
import delivery.app.entities.Beverage;
import delivery.app.repositories.OrderRepository;
import delivery.app.services.MenuService;
import delivery.app.services.OrderService;
import jakarta.transaction.Transactional;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
public class OrderControllerIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private OrderService orderService;
    
    private Long employeeId;
    private Long beverageId;
    private Long orderId;

    
    @BeforeEach
    public void setUp(TestInfo testInfo) {
  
        String waiterEmail = "email@email.com";
        Beverage beverage = new Beverage("Coke", 10.f);
        beverage = menuService.saveBeverage(beverage);
        this.beverageId = beverage.getBeverageId();
        
        if (testInfo.getTestMethod().isPresent() &&
                testInfo.getTestMethod().get().getName().equals("testDeleteOrderById")) {
            OrderRequestDTO orderRequest = new OrderRequestDTO(null, null, null,
                    beverageId, waiterEmail, null);
            OrderResponseDTO response = orderService.saveOrder(orderRequest);
            this.orderId = response.getOrderId();
        }
    }


    @Test
    public void testFindOrderById() throws Exception {
        OrderRequestDTO orderRequest = new OrderRequestDTO(null, null, null,
                beverageId, "waiter_email", null);
        OrderResponseDTO response = orderService.saveOrder(orderRequest);

        mockMvc.perform(get("/orders/" + response.getOrderId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders_id").value(response.getOrderId()));
    }

    @Test
    public void testDeleteOrderById() throws Exception {
        mockMvc.perform(delete("/orders/" + orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        boolean orderExists = orderRepository.existsById(orderId);
        assertFalse(orderExists, "The order should be deleted");
    }
  
    @Test
    public void testGetOrders() throws Exception {
        OrderRequestDTO orderRequest = new OrderRequestDTO(null, null, null,
                beverageId, "waiter_email", null);
        orderService.saveOrder(orderRequest);
        mockMvc.perform(get("/orders?page=0&size=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
        
    }
    
     @Test
    public void testAddOrder() throws Exception {
        OrderRequestDTO orderRequest = new OrderRequestDTO(null, null,
        		null, beverageId, "waiter_email",null);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
           		.andExpect(jsonPath("$.orderId").value(1L))
           		.andExpect(jsonPath("$.waiterEmail").value("waiter_email"));
    }
}
