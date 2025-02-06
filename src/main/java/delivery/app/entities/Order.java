package delivery.app.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orders_id")
	@JsonProperty("orders_id")
	private Long orderId;
	@JsonProperty("created_at")
	@Column(name="created_at")
	private LocalDateTime createdAt;
	@Column(name="main_course_price")
	private float mainCoursePrice;
	@Column(name="dessert_price")
	private float dessertPrice;
	@Column(name="beverage_price")
	private float beveragePrice;
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "lunch_id")
	private Lunch lunch;
	
	@Enumerated(EnumType.STRING)
	@Column(name="main_course_cuisine")
	private CuisineType mainCourseCuisine;

	@Enumerated(EnumType.STRING)
	@Column(name="dessert_cuisine")
	private CuisineType dessertCuisine;

	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "beverage_id")
	private Beverage beverage;

	@Column(name = "waiter_email", nullable = false)
	private String waiterEmail;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
	@JoinColumn(name = "orders_id")
	private List<OrderedAdditional> orderedBeverageAdditionals;


}
