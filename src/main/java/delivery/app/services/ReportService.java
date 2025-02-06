package delivery.app.services;


import com.itextpdf.text.DocumentException;
import delivery.app.dto.ReportRequest;
import delivery.app.entities.Order;
import delivery.app.reports.PdfReportCreator;
import delivery.app.reports.Report;
import delivery.app.reports.ReportCreator;
import delivery.app.reports.XlsReportCreator;
import delivery.app.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    public byte[] createReport(ReportRequest reportRequest) throws DocumentException, IOException {
            List<Order> orders = this.getOrdersByWaiterEmailAndDate(reportRequest);
            ReportCreator reportCreator = new ReportCreator();
            if(reportRequest.getReportType().equals("pdf")){
                reportCreator = new PdfReportCreator();
            }
            else if(reportRequest.getReportType().equals("xls")){
                reportCreator = new XlsReportCreator();
            }
            Report report = reportCreator.createReport();
            return  report.generateReport(orders, reportRequest);
    }

    private List<Order> getOrdersByWaiterEmailAndDate(ReportRequest reportRequest) {
        String waiterEmail = reportRequest.getWaiterEmail();
        String startDateAsString = reportRequest.getStartDate() + "T" + "00:00:01";
        LocalDateTime startDate = LocalDateTime.parse(startDateAsString);
        String endDateAsString = reportRequest.getEndDate() + "T" + "23:59:59";
        LocalDateTime endDate = LocalDateTime.parse(endDateAsString);

        if ("all".equals(waiterEmail)) {
            return orderRepository.findByCreatedAtBetween(startDate, endDate);
        } else {
            return orderRepository.findByWaiterEmailAndCreatedAtBetween(waiterEmail, startDate, endDate);
        }
    }
}
