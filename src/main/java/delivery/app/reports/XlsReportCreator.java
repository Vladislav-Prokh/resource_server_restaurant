package delivery.app.reports;

public class XlsReportCreator extends ReportCreator {

    @Override
    public Report createReport() {
        return new XlsReport();
    }
}
