package delivery.app.dto;

import java.util.List;

import delivery.app.entities.CuisineType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

	private Long lunchId;
	@Enumerated(EnumType.STRING)
	private CuisineType mainCourseCuisine;
	@Enumerated(EnumType.STRING)
	private CuisineType dessertCuisine;
	private Long beverageId;
	private Long waiterId;
	private List<OrderedAdditionalDTO> orderedAdditions;

}
