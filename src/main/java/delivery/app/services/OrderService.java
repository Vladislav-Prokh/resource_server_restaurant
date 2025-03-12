package delivery.app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import delivery.app.dto.UpdateEmailRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import delivery.app.dto.OrderRequestDTO;
import delivery.app.dto.OrderResponseDTO;
import delivery.app.dto.OrderedAdditionalDTO;
import delivery.app.entities.Beverage;
import delivery.app.entities.BeverageAdditional;
import delivery.app.entities.Lunch;
import delivery.app.entities.Order;
import delivery.app.entities.OrderedAdditional;
import delivery.app.exceptions.ResourceNotFoundException;
import delivery.app.repositories.BeverageAdditionalRepository;
import delivery.app.repositories.BeverageRepository;
import delivery.app.repositories.LunchRepository;
import delivery.app.repositories.OrderRepository;
import delivery.app.repositories.OrderedAdditionalRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class OrderService {


	private final OrderRepository orderRepository;
	private final BeverageRepository beverageRepository;
	private final LunchRepository lunchRepository;
	private final BeverageAdditionalRepository beverageAdditionalRepository;
	private final OrderedAdditionalRepository orderedAdditionalRepository;

	public OrderService(OrderRepository orderRepository, BeverageRepository beverageRepository, LunchRepository lunchRepository, BeverageAdditionalRepository beverageAdditionalRepository, OrderedAdditionalRepository orderedAdditionalRepository) {
		this.orderRepository = orderRepository;
		this.beverageRepository = beverageRepository;
		this.lunchRepository = lunchRepository;
		this.beverageAdditionalRepository = beverageAdditionalRepository;
		this.orderedAdditionalRepository = orderedAdditionalRepository;
	}

	public OrderResponseDTO saveOrder(OrderRequestDTO orderDTO) {
	    Beverage orderedBeverage = fetchBeverageIfPresent(orderDTO.getBeverageId());
	    Lunch lunch = fetchLunchIfPresent(orderDTO.getLunchId());
	    String servicingWaiter = orderDTO.getWaiterEmail();

	    Order newOrder = createOrder(orderDTO, orderedBeverage, lunch, servicingWaiter);
	    newOrder = orderRepository.save(newOrder);

	    List<OrderedAdditional> orderedAdditionals = saveOrderedAdditionals(orderDTO, newOrder);

	    if (!orderedAdditionals.isEmpty()) {
	        newOrder.setOrderedBeverageAdditionals(orderedAdditionals);
	    } else {
	        newOrder.setOrderedBeverageAdditionals(Collections.emptyList());
	    }
	    newOrder = orderRepository.save(newOrder);
	    List<OrderedAdditionalDTO> orderedAdditionalsDTO = mapToOrderedAdditionalDTOs(orderedAdditionals);
	    return buildOrderResponseDTO(orderDTO, newOrder, lunch, orderedBeverage, servicingWaiter, orderedAdditionalsDTO);
	}

	private Beverage fetchBeverageIfPresent(Long beverageId) {
	    return beverageId != null ? fetchBeverage(beverageId) : null;
	}

	private Lunch fetchLunchIfPresent(Long lunchId) {
	    return lunchId != null ? fetchLunch(lunchId) : null;
	}

	private Order createOrder(OrderRequestDTO orderDTO, Beverage orderedBeverage, Lunch lunch, String servicingWaiter) {
	    Order order = new Order();
	    order.setWaiterEmail(servicingWaiter);
	    order.setBeverage(orderedBeverage);

	    if (lunch != null) {
	        order.setLunch(lunch);
	        setLunchDetails(order, lunch, orderDTO);
	    }
	    order.setCreatedAt(LocalDateTime.now());
	    order.setBeveragePrice(orderedBeverage != null ? orderedBeverage.getBeveragePrice() : 0.0f);
	    order.setMainCoursePrice(calculateMainCoursePrice(lunch));
	    order.setDessertPrice(calculateDessertPrice(lunch));

	    return order;
	}

	private void setLunchDetails(Order order, Lunch lunch, OrderRequestDTO orderDTO) {
	    if (lunch.getDessert() != null) {
	        order.setDessertCuisine(orderDTO.getDessertCuisine());
	    }
	    if (lunch.getMainCourse() != null) {
	        order.setMainCourseCuisine(orderDTO.getMainCourseCuisine());
	    }
	}

	private float calculateMainCoursePrice(Lunch lunch) {
	    return (lunch != null && lunch.getMainCourse() != null) ? lunch.getMainCourse().getMealPrice() : 0.0f;
	}

	private float calculateDessertPrice(Lunch lunch) {
	    return (lunch != null && lunch.getDessert() != null) ? lunch.getDessert().getDessertPrice() : 0.0f;
	}

	private List<OrderedAdditional> saveOrderedAdditionals(OrderRequestDTO orderDTO, Order newOrder) {
	    if (orderDTO.getOrderedAdditions() == null) {
	        return new ArrayList<>();
	    }

	    List<OrderedAdditional> orderedAdditionals = orderDTO.getOrderedAdditions().stream()
	        .filter(Objects::nonNull)
	        .map(additionalDTO -> createOrderedAdditional(additionalDTO, newOrder))
	        .collect(Collectors.toList());

	    return orderedAdditionalRepository.saveAll(orderedAdditionals);
	}

	private List<OrderedAdditionalDTO> mapToOrderedAdditionalDTOs(List<OrderedAdditional> orderedAdditionals) {
	    return orderedAdditionals.stream()
	        .map(orderedAdditional -> new OrderedAdditionalDTO(
	            orderedAdditional.getBeverage().getBeverageId(),
	            orderedAdditional.getBeverageAdditional().getBeverageAdditionalId(),
	            orderedAdditional.getQuantity()
	        ))
	        .collect(Collectors.toList());
	}

	OrderResponseDTO buildOrderResponseDTO(OrderRequestDTO orderDTO, Order newOrder, Lunch lunch, Beverage orderedBeverage, String servicingWaiter, List<OrderedAdditionalDTO> orderedAdditionalsDTO) {
	    return new OrderResponseDTO(
	        newOrder.getOrderId(),
	        lunch != null && lunch.getMainCourse() != null ? orderDTO.getMainCourseCuisine() : null,
	        lunch != null && lunch.getDessert() != null ? orderDTO.getDessertCuisine() : null,
	        lunch != null ? lunch.getLunchId() : null,
	        orderedBeverage != null ? orderedBeverage.getBeverageId() : null,
                servicingWaiter,
	        orderedAdditionalsDTO
	    );
	}

	private Beverage fetchBeverage(Long beverageId) {
	    return beverageRepository.findById(beverageId)
	        .orElseThrow(() -> new IllegalArgumentException("Invalid beverage ID"));
	}

	private Lunch fetchLunch(Long lunchId) {
	    return lunchRepository.findById(lunchId)
	        .orElseThrow(() -> new IllegalArgumentException("Invalid lunch ID"));
	}

	private OrderedAdditional createOrderedAdditional(OrderedAdditionalDTO additionalDTO, Order order) {
	    Beverage beverage = fetchBeverage(additionalDTO.getBeverageId());
	    BeverageAdditional beverageAdditional = beverageAdditionalRepository.findById(additionalDTO.getAdditionId())
	        .orElseThrow(() -> new IllegalArgumentException("Invalid additional ID"));
	    return new OrderedAdditional(beverage, order, beverageAdditional, additionalDTO.getQuantity());
	}

	public Order findOrderById(Long order_id) {
		return this.orderRepository.findById(order_id)
				.orElseThrow(() -> new ResourceNotFoundException("resource not found"));
	}

	public Page<Order> getOrders(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return orderRepository.findAll(pageable);
	}
	
	@Transactional
	public void deleteOrder(Long orderId) {
	    Order order = orderRepository.findById(orderId)
	        .orElseThrow(() -> new EntityNotFoundException("Order not found"));
	    if (order.getOrderedBeverageAdditionals() != null) {
	        order.getOrderedBeverageAdditionals().clear(); 
	    }
	    orderRepository.save(order);
	    orderRepository.delete(order);
	}

	@RabbitListener(queues = "emailQueue")
	public void processEmail(UpdateEmailRequest request) {
		this.orderRepository.updateWaiterEmailInOrders(request.getOldEmail(), request.getNewEmail());
	}
}
