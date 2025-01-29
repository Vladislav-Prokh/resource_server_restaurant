package delivery.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import delivery.app.entities.BeverageAdditional;

@Repository
public interface BeverageAdditionalRepository extends JpaRepository<BeverageAdditional,Long> {

}
