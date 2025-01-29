package delivery.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LunchResponseDTO {

    private Long lunchId;
    private Long mainCourseId;
    private Long dessertId;

}
