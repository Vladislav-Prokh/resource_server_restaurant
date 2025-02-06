package delivery.app.reports;

public  class ReportCreator {
    public Report createReport() {
        return new PdfReport();
    }
}
