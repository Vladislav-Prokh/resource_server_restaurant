package delivery.app.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "beverages")
@Getter
@Setter
@NoArgsConstructor
public class Beverage {
	@JsonProperty("beverage_id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "beverage_id")
	private Long beverageId;
	@Column(length = 100, name = "beverage_name")
	private String beverageName;
	@Column(name = "beverage_price")
	private float beveragePrice;
	@Column(name = "description" , length = 300)
	private String description;
	@JsonIgnore
	@OneToMany(mappedBy = "beverage")
	private List<OrderedAdditional> orderedAdditionals;


	public Beverage(String beverageName, float beveragePrice, List<OrderedAdditional> orderedAdditionals) {
		super();
		this.beverageName = beverageName;
		this.beveragePrice = beveragePrice;
		this.orderedAdditionals = orderedAdditionals;
	}

	public Beverage(String beverageName, String description,float beveragePrice) {
		super();
		this.beverageName = beverageName;
		this.description = description;
		this.beveragePrice = beveragePrice;
	}

}
