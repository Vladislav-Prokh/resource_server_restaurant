package delivery.app.services;

import delivery.app.dto.OrderRequestDTO;
import delivery.app.dto.OrderResponseDTO;
import delivery.app.entities.Beverage;
import delivery.app.entities.BeverageAdditional;
import delivery.app.entities.CuisineType;
import delivery.app.entities.Dessert;
import delivery.app.entities.Employee;
import delivery.app.entities.Lunch;
import delivery.app.entities.Meal;
import delivery.app.entities.Order;
import delivery.app.entities.OrderedAdditional;
import delivery.app.entities.Role;
import delivery.app.exceptions.ResourceNotFoundException;
import delivery.app.repositories.BeverageRepository;
import delivery.app.repositories.EmployeeRepository;
import delivery.app.repositories.LunchRepository;
import delivery.app.repositories.OrderRepository;
import delivery.app.repositories.OrderedAdditionalRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class OrderServiceTest {

	    @Mock
	    private OrderRepository orderRepository;

	    @Mock
	    private BeverageRepository beverageRepository;

	    @Mock
	    private EmployeeRepository employeeRepository;

	    @Mock
	    private LunchRepository lunchRepository;

	    @Mock
	    private OrderedAdditionalRepository orderedAdditionalRepository;

	    @InjectMocks
	    private OrderService orderService;

	    private OrderRequestDTO orderRequestDTO;
	    private Order order;
    	private List<OrderedAdditional> orderedAdditionals;

	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.openMocks(this);
            Beverage beverage = new Beverage();
	        beverage.setBeverageId(1L);
	        beverage.setBeverageName("Coke");

            BeverageAdditional beverageAdditional = new BeverageAdditional();
	        beverageAdditional.setBeverageAdditionalId(1L);
	        beverageAdditional.setBeverageAdditionalName("ice");
	        beverageAdditional.setBeverageAdditionalPrice(10.0f);

            Meal mainCourse = new Meal();
	        mainCourse.setMealId(1L);
	        mainCourse.setMealName("Pasta");

            Dessert dessert = new Dessert();
	        dessert.setDessertId(1L);
	        dessert.setDessertName("Cake");

            Lunch lunch = new Lunch(mainCourse, dessert);
	        lunch.setLunchId(1L);
            Employee waiter = new Employee(1L, "John", "Doe", "developer", Role.ADMIN);
	        
	        OrderedAdditional orderedAdditional = new OrderedAdditional(beverage, order, beverageAdditional, 1); 
	        orderedAdditional.setOrderedAdditional_id(1L);
	        orderedAdditionals = List.of(orderedAdditional);
	        
	        orderRequestDTO = new OrderRequestDTO(
	                1L, CuisineType.Italian, CuisineType.Italian, 1L, 1L, Collections.emptyList()
	        );

	        order = new Order(1L, LocalDateTime.now(), 10.0f, 10.0f, 10.0f,
                    lunch, CuisineType.Italian, CuisineType.Mexican, beverage, waiter, orderedAdditionals);

	        when(beverageRepository.findById(1L)).thenReturn(java.util.Optional.of(beverage));
	        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(waiter));
	        when(lunchRepository.findById(1L)).thenReturn(java.util.Optional.of(lunch));
	        when(orderRepository.save(any(Order.class))).thenReturn(order);
	        when(orderedAdditionalRepository.saveAll(anyList())).thenReturn(orderedAdditionals); 
	    }
	    
	    @Test
	    public void testSaveOrder() {
	  
	    	        OrderResponseDTO result = orderService.saveOrder(orderRequestDTO);

	    	        assertNotNull(result);
	    	        assertEquals(1L, result.getOrderId());
	    	        assertEquals(CuisineType.Italian, result.getMainCourseCuisine());
	    	        assertEquals(CuisineType.Italian, result.getDessertCuisine());
	    	        assertEquals(1L, result.getLunchId());
	    	        assertEquals(1L, result.getBeverageId());
	    	        assertEquals(1L, result.getWaiterId());
	    	        assertEquals(orderedAdditionals.size(), result.getOrderedAdditions().size());

	    	        verify(orderRepository, times(2)).save(any(Order.class)); 
	    	        verify(beverageRepository, times(1)).findById(1L);
	    	        verify(lunchRepository, times(1)).findById(1L);
	    	        verify(employeeRepository, times(1)).findById(1L);
	    	        verify(orderedAdditionalRepository, times(1)).saveAll(anyList());  
	    }
	    
	    @Test
	    void testFindOrderById_Found() {
	
	        Long orderId = 1L;
	        Order order = new Order();
	        order.setOrderId(orderId);
	        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

	        Order result = orderService.findOrderById(orderId);

	        assertNotNull(result);
	        assertEquals(orderId, result.getOrderId());
	        verify(orderRepository, times(1)).findById(orderId);
	    }

	    @Test
	    void testFindOrderById_NotFound() {
	        Long orderId = 1L;
	        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

	        assertThrows(ResourceNotFoundException.class, () -> orderService.findOrderById(orderId));
	        verify(orderRepository, times(1)).findById(orderId);
	    }
	    
	    @Test
	    void testGetOrders() {
	        int page = 0;
	        int size = 10;
	        Pageable pageable = PageRequest.of(page, size);
	        Order order1 = new Order();
	        Order order2 = new Order();
	        Page<Order> ordersPage = new PageImpl<>(List.of(order1, order2), pageable, 2);
	        when(orderRepository.findAll(pageable)).thenReturn(ordersPage);

	        Page<Order> result = orderService.getOrders(page, size);

	        assertNotNull(result);
	        assertEquals(2, result.getContent().size());
	        verify(orderRepository, times(1)).findAll(pageable);
	    }

	    @Test
	    @Transactional
	    void testDeleteOrder_Found() {
	        Long orderId = 1L;

	        Order order = new Order();
	        order.setOrderId(orderId);
	        order.setOrderedBeverageAdditionals(new ArrayList<>());
	        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

	        orderService.deleteOrder(orderId);

	        verify(orderRepository, times(1)).findById(orderId);  
	        verify(orderRepository, times(1)).delete(order);
	    }

	    @Test
	    void testDeleteOrder_NotFound() {

	        Long orderId = 1L;
	        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

	        assertThrows(EntityNotFoundException.class, () -> orderService.deleteOrder(orderId));
	        verify(orderRepository, times(1)).findById(orderId);
	    }
	    
}

