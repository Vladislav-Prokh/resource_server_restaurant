package delivery.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import delivery.app.entities.Order;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository <Order, Long>{
    List<Order> findByWaiterEmailAndCreatedAtBetween(String waiterEmail, LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
