package delivery.app.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "meals")
public class Meal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("meal_id")
	private Long mealId;
	@Column(length = 50,name = "meal_name")
	private String mealName;
	@Column(name = "meal_price")
	private float mealPrice;
	@Column(name = "description",length = 500)
	private String description;

	public Meal(String mealName, float mealPrice) {
		super();
		this.mealName = mealName;
		this.mealPrice = mealPrice;
	}

    public Meal(String mealName, String mealDescription, float mealPrice) {
		this.mealName = mealName;
		this.mealPrice = mealPrice;
		this.description = mealDescription;
    }
}
