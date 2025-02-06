package delivery.app.reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import delivery.app.dto.ReportRequest;
import delivery.app.entities.Order;
import delivery.app.entities.OrderedAdditional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PdfReport extends Report{
    @Override
    public byte[] generateReport(List<Order> orders, ReportRequest reportRequest) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titlesFont = createFont(FontFactory.COURIER, 16);
            Font textFont = createFont(FontFactory.COURIER, 14);

            document.add(createParagraph("Report for " + reportRequest.getWaiterEmail(), titlesFont, Element.ALIGN_CENTER));
            document.add(Chunk.NEWLINE);
            document.add(createParagraph("Report created for orders since " + reportRequest.getStartDate() + " to " + reportRequest.getEndDate(), textFont, Element.ALIGN_LEFT));
            document.add(Chunk.NEWLINE);

            if (!orders.isEmpty()) {
                document.add(createParagraphTable(orders, textFont));
                document.add(Chunk.NEWLINE);
                document.add(getStatisticParagraph(orders, textFont));
                document.add(Chunk.NEWLINE);
            }
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    private Font createFont(String fontType, int size) {
        return FontFactory.getFont(fontType, size, BaseColor.BLACK);
    }

    private Paragraph createParagraph(String text, Font font, int alignment) {
        Paragraph paragraph = new Paragraph(new Chunk(text, font));
        paragraph.setAlignment(alignment);
        return paragraph;
    }

    private static Paragraph getStatisticParagraph(List<Order> orders, Font textFont) {
        Map<LocalDateTime, List<Order>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(Order::getCreatedAt));

        int totalOrders = orders.size();
        float totalSum = 0.0f;
        for(Order order : orders){
            totalSum += order.getBeveragePrice()+ order.getDessertPrice()+ order.getMainCoursePrice();
            for(OrderedAdditional additional: order.getOrderedBeverageAdditionals()){
                totalSum+= additional.getQuantity()*additional.getBeverageAdditional().getBeverageAdditionalPrice();
            }
        }
        int averageOrderPerDay = totalOrders / groupedByDate.size();
        float averageIncomePerDay = totalSum/ groupedByDate.size();
        String statisticInfo = "Total orders: " + totalOrders +
                "\nTotal sum: " + String.format("%.2f", totalSum)+
                "\nAverage orders per day: " + averageOrderPerDay+
                "\nAverage income per day: " + String.format("%.2f", averageIncomePerDay);

        Chunk statisticChunk = new Chunk(statisticInfo, textFont);
        Paragraph statisticParagraph = new Paragraph(statisticChunk);
        statisticParagraph.setAlignment(Element.ALIGN_LEFT);
        return statisticParagraph;
    }

    private PdfPTable createParagraphTable(List<Order> orders, Font textFont){
        PdfPTable table = new PdfPTable(3);
        addTableHeader(table);
        Map<LocalDateTime, List<Order>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(Order::getCreatedAt));
        groupedByDate.forEach((date, orderList) -> {
            table.addCell(date.toString().substring(0,16).replace("T"," "));
            table.addCell(String.valueOf(orderList.size()));
            float totalSumByDate = 0.0f;
            for(Order order : orderList){
                totalSumByDate+= order.getMainCoursePrice()+ order.getDessertPrice()+ order.getBeveragePrice();
                for(OrderedAdditional additional: order.getOrderedBeverageAdditionals()){
                    totalSumByDate += additional.getQuantity() * additional.getBeverageAdditional().getBeverageAdditionalPrice();
                }
            }
            table.addCell(String.format("%.2f", totalSumByDate));
        });
     return table;
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Date", "orders", "total cost")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }
}
