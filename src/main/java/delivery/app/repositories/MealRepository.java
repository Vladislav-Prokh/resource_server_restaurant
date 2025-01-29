package delivery.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import delivery.app.entities.Meal;

@Repository
public interface MealRepository extends JpaRepository<Meal,Long>{

}
