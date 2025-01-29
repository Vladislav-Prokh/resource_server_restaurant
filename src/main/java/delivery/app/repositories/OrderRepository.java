package delivery.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import delivery.app.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository <Order, Long>{

}
