package delivery.app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import delivery.app.dto.OrderRequestDTO;
import delivery.app.dto.OrderResponseDTO;
import delivery.app.entities.Order;
import delivery.app.services.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public OrderResponseDTO addOrder(@RequestBody OrderRequestDTO order) {
		return this.orderService.saveOrder(order);
	}

	@GetMapping("/{order-id}")
	public Order findOrderById(@PathVariable("order-id") Long order_id)  {
		return this.orderService.findOrderById(order_id);
	}
	@ResponseStatus(HttpStatus.NO_CONTENT) 
	@DeleteMapping("/{order-id}")
	public void deleteOrderById(@PathVariable("order-id") Long order_id){
		this.orderService.deleteOrder(order_id);
	}
	
	@GetMapping
    public Page<Order> getOrders(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrders(page, size);
    }
}
