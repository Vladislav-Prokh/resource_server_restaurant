package delivery.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import delivery.app.entities.Lunch;

@Repository
public interface LunchRepository extends JpaRepository<Lunch,Long>{

}
