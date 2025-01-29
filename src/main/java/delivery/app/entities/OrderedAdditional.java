package delivery.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ordered_additionals")
public class OrderedAdditional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderedAdditional_id;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "beverage_id", nullable = false)
    private Beverage beverage;
   
    @ManyToOne
    @JoinColumn(name = "orders_id")
    @JsonIgnore
    private Order order;
    @JsonProperty("beverageAdditional")
    @ManyToOne
    @JoinColumn(name = "beverage_additional_id", nullable = false)
    private BeverageAdditional beverageAdditional;

    @Column(nullable = false)
    private int quantity;

    public OrderedAdditional(Beverage beverage, Order order, BeverageAdditional beverageAdditional, int quantity) {
        this.beverage = beverage;
        this.order = order;
        this.beverageAdditional = beverageAdditional;
        this.quantity = quantity;
    }

}
