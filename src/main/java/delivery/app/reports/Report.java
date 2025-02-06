package delivery.app.reports;


import com.itextpdf.text.DocumentException;
import delivery.app.dto.ReportRequest;
import delivery.app.entities.Order;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

@Getter
@Setter
public abstract class Report {
    public abstract  byte[] generateReport(List<Order> orders, ReportRequest reportRequest) throws DocumentException, IOException;
}
