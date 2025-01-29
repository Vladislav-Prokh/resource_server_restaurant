package delivery.app.dto;

import java.util.List;

import delivery.app.entities.CuisineType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class OrderResponseDTO {
    private Long orderId;
	@Enumerated(EnumType.STRING)
	private CuisineType mainCourseCuisine;
	@Enumerated(EnumType.STRING)
	private CuisineType dessertCuisine;
	private Long lunchId;
    private Long beverageId;
    private Long waiterId;
    private List<OrderedAdditionalDTO> orderedAdditions;
}
