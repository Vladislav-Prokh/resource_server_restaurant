package delivery.app.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import delivery.app.entities.Order;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository <Order, Long>{
    List<Order> findByWaiterEmailAndCreatedAtBetween(String waiterEmail, LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Query("UPDATE Order o SET o.waiterEmail = :newWaiterEmail WHERE o.waiterEmail = :oldWaiterEmail")
    @Transactional
    void updateWaiterEmailInOrders(@Param("oldWaiterEmail") String oldWaiterEmail,
                                  @Param("newWaiterEmail") String newWaiterEmail);
}
