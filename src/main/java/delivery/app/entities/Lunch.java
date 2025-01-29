package delivery.app.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lunches")
public class Lunch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lunch_id")
	private Long lunchId;
    @OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "meal_id")
	@OnDelete(action = OnDeleteAction.SET_NULL)
	private Meal mainCourse;
    @OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "dessert_id")
	@OnDelete(action = OnDeleteAction.SET_NULL)
	private Dessert dessert;

	public Lunch(Meal mainCourse, Dessert dessert) {
		this.mainCourse = mainCourse;
		this.dessert = dessert;
	}

}
