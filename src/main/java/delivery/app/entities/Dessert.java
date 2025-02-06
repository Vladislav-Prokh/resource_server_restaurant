package delivery.app.entities;

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
@Table(name = "desserts")
public class Dessert {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dessert_id")
	private Long dessertId;
	@Column(length = 50,name = "dessert_name")
	private String dessertName;
	@Column(length = 50,name = "dessert_price")
	private float dessertPrice;
	@Column(name = "description", length = 500)
	private String description;

	public Dessert(String dessertName, float dessertPrice) {
		super();
		this.dessertName = dessertName;
		this.dessertPrice = dessertPrice;
	}

    public Dessert(String dessertName, String dessertDescription, float dessertPrice) {
		this.dessertName = dessertName;
		this.dessertPrice = dessertPrice;
		this.description = dessertDescription;
    }
}
