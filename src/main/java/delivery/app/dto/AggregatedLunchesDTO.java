package delivery.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AggregatedLunchesDTO {

    int amountLessEdge;
    int amountGreaterEdge;
}
