package delivery.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {
    private String reportType;
    private String startDate;
    private String endDate;
    private String waiterEmail;
}
