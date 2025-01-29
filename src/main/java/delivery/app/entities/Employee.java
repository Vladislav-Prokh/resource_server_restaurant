package delivery.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long employeeId;
	@Column(length = 20,name = "employee_name")
	private String employeeName;
	@Column(name = "employee_surname", nullable = false)
	private String employeeSurName;
	@Column(length = 50, name = "employee_email")
	private String employeeEmail;
	@Enumerated(EnumType.STRING)
	@Column(name = "employee_role")
	private Role role;

}
