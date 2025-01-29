package delivery.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import delivery.app.entities.Beverage;

@Repository
public interface BeverageRepository extends JpaRepository<Beverage,Long>{
	

}
