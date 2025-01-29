package delivery.app.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "beverage_additionals")
public class BeverageAdditional {

	@JsonProperty("beverage_additional_id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "beverage_additional_id")
	private Long beverageAdditionalId;

	@Column(length = 20, nullable = false, name = "beverage_additional_name")
	private String beverageAdditionalName;

	@Column(nullable = false, name = "beverage_additional_price")
	private float beverageAdditionalPrice;

}
