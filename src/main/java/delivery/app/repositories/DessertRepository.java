package delivery.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import delivery.app.entities.Dessert;

@Repository
public interface DessertRepository extends JpaRepository<Dessert,Long>{

}
