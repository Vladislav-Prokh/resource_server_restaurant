package delivery.app.reports;

import delivery.app.dto.ReportRequest;
import delivery.app.entities.Order;
import delivery.app.entities.OrderedAdditional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class XlsReport extends Report{

    private int globalRowIndex = 0;
    @Override
    public byte[] generateReport(List<Order> orders, ReportRequest reportRequest) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(reportRequest.getWaiterEmail()+".xls");
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);
            Row header = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            headerStyle.setFont(font);
            Row row = sheet.createRow(globalRowIndex);
            globalRowIndex++;
            row.createCell(0).setCellValue("waiter email");
            row.createCell(1).setCellValue(reportRequest.getWaiterEmail());
            createOrderTableStatistic(orders,sheet);
            createOrderAverageStatistic(orders,sheet);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    private void createOrderTableStatistic(List<Order> orders, Sheet sheet){
        Map<LocalDateTime, List<Order>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(Order::getCreatedAt));
        Row[] rows = new Row[groupedByDate.size()];
        AtomicInteger rowIndex = new AtomicInteger(0);
        groupedByDate.forEach((date, orderList) -> {
            int currentIndex = rowIndex.getAndIncrement();
            rows[currentIndex] = sheet.createRow(globalRowIndex++);
            rows[currentIndex].createCell(0).setCellValue(date.toString().substring(0,16).replace("T"," "));
            rows[currentIndex].createCell(1).setCellValue(orderList.size());
            float ordersTotalSumByDate = 0f;
            for (Order order : orderList) {
                ordersTotalSumByDate+= order.getBeveragePrice()+order.getDessertPrice()+ order.getMainCoursePrice();
                for(OrderedAdditional additional: order.getOrderedBeverageAdditionals()){
                    ordersTotalSumByDate += additional.getQuantity()* additional.getBeverageAdditional().getBeverageAdditionalPrice();
                }
            }
            rows[currentIndex].createCell(2).setCellValue(String.format("%.2f", ordersTotalSumByDate));
        });
    }
    private void createOrderAverageStatistic(List<Order> orders, Sheet sheet){
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

        Row rowTotalOrders = sheet.createRow(globalRowIndex++);
        rowTotalOrders.createCell(0).setCellValue("total orders");
        rowTotalOrders.createCell(1).setCellValue(totalOrders);

        Row rowTotalSum = sheet.createRow(globalRowIndex++);
        rowTotalSum.createCell(0).setCellValue("total sum");
        rowTotalSum.createCell(1).setCellValue(String.format("%.2f",totalSum));

        Row rowAverageOrders = sheet.createRow(globalRowIndex++);
        rowAverageOrders.createCell(0).setCellValue("average orders");
        rowAverageOrders.createCell(1).setCellValue(averageOrderPerDay);

        Row rowAverageIncome = sheet.createRow(globalRowIndex++);
        rowAverageIncome.createCell(0).setCellValue("average income");
        rowAverageIncome.createCell(1).setCellValue(String.format("%.2f",averageIncomePerDay));
    }
}
