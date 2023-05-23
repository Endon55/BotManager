package core.database.access.reports;

import core.database.access.Dao;
import core.database.tables.reports.TrekkingReport;
import org.hibernate.SessionFactory;

public class TrekkingReports extends Dao<TrekkingReport, Integer>
{
    public TrekkingReports(SessionFactory sessionFactory)
    {
        super(sessionFactory, TrekkingReport.class, "Trekking_Reports");
    }
}
