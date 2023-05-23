package core.database.access.reports;

import core.database.access.Dao;
import core.database.tables.reports.MuleReport;
import org.hibernate.SessionFactory;

public class MuleReports extends Dao<MuleReport, Integer>
{
    public MuleReports(SessionFactory sessionFactory)
    {
        super(sessionFactory, MuleReport.class, "Mule_Reports");
    }
    
    
    public synchronized int getJobsCompleted(int muleID)
    {
        int jobsComplete = 0;
        for(MuleReport report : getAll())
        {
            if(report.getMule().getId() == muleID) jobsComplete++;
        }
        return jobsComplete;
    }
    
}
