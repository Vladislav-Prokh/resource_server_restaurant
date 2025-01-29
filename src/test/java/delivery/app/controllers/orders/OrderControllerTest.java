package delivery.app.controllers.orders;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import delivery.app.dto.OrderRequestDTO;
import delivery.app.dto.OrderResponseDTO;
import delivery.app.controllers.OrderController;
import delivery.app.entities.Order;
import delivery.app.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(roles = "ADMIN")
public class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private OrderService orderService;

	private ObjectMapper objectMapper;

    @BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
        OrderController orderController = new OrderController(orderService);
		mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	public void testAddOrder() throws Exception {
		OrderRequestDTO requestDTO = new OrderRequestDTO();
		requestDTO.setBeverageId(1L);
		requestDTO.setLunchId(1L);
		requestDTO.setWaiterId(1L);

		OrderResponseDTO responseDTO = new OrderResponseDTO();
		responseDTO.setOrderId(2L);

		when(orderService.saveOrder(any(OrderRequestDTO.class))).thenReturn(responseDTO);

		mockMvc.perform(post("/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderId").value(2L));

		verify(orderService, times(1)).saveOrder(any(OrderRequestDTO.class));
	}

	@Test
	public void testFindOrderById() throws Exception {
		Long orderId = 1L;
		Order order = new Order();
		order.setOrderId(orderId);

		when(orderService.findOrderById(orderId)).thenReturn(order);

		mockMvc.perform(get("/orders/{order-id}", orderId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orders_id").value(orderId));

		verify(orderService, times(1)).findOrderById(orderId);
	}

	@Test
	public void testDeleteOrderById() throws Exception {
		Long orderId = 1L;

		doNothing().when(orderService).deleteOrder(orderId);

		mockMvc.perform(delete("/orders/{order-id}", orderId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(orderService, times(1)).deleteOrder(orderId);
	}

	@Test
	public void testGetOrders() throws Exception {
		int page = 0;
		int size = 2;

		Order order1 = new Order();
		order1.setOrderId(1L);
		order1.setCreatedAt(LocalDateTime.now());

		Order order2 = new Order();
		order2.setOrderId(2L);
		order2.setCreatedAt(LocalDateTime.now());

		Page<Order> orderPage = new PageImpl<>(Arrays.asList(order1, order2), PageRequest.of(page, size), 2);

		when(orderService.getOrders(page, size)).thenReturn(orderPage);

		mockMvc.perform(get("/orders")
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(size))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].orders_id").value(1L))
				.andExpect(jsonPath("$.content[1].orders_id").value(2L))
				.andExpect(jsonPath("$.content.length()").value(2));

		verify(orderService, times(1)).getOrders(page, size);
	}
}