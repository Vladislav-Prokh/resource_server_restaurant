package delivery.app.controllers;
import com.itextpdf.text.DocumentException;
import delivery.app.dto.ReportRequest;
import delivery.app.services.ReportService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping()
    public ResponseEntity<byte[]> createReportPdf(@RequestBody ReportRequest report) throws DocumentException, IOException {
        byte[] pdfBytes = this.reportService.createReport(report);

        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = getMediaTypeByReportType(report.getReportType());
        headers.setContentType(mediaType);
        String filename = "report" + report.getReportType();
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    public MediaType getMediaTypeByReportType(String reportType) {
        return switch (reportType.toLowerCase()) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "xlsx" -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "xls" -> MediaType.valueOf("application/vnd.ms-excel");
            case "docx" -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "doc" -> MediaType.valueOf("application/msword");
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "json" -> MediaType.APPLICATION_JSON;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}
