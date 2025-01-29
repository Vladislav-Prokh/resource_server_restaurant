package delivery.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderedAdditionalDTO {

	private Long beverageId;
	private Long additionId;
	private int quantity;
	
}
