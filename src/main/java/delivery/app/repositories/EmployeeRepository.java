package delivery.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import delivery.app.entities.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long>{
	Employee findByEmployeeEmail(String employeeName);
}
