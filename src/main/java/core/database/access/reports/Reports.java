package core.database.access.reports;

import core.database.tables.reports.MuleReport;
import core.database.tables.reports.TrekkingReport;
import core.database.tables.reports.WorkerReport;
import org.hibernate.SessionFactory;

import java.time.ZonedDateTime;
import java.util.List;

public class Reports
{
    
    private final WorkerReports workerReports;
    private final MuleReports muleReports;
    private final TrekkingReports trekkingReports;
    
    public Reports(SessionFactory sessionFactory)
    {
        workerReports = new WorkerReports(sessionFactory);
        muleReports = new MuleReports(sessionFactory);
        trekkingReports = new TrekkingReports(sessionFactory);
    }
    
    public synchronized List<WorkerReport> getAllWorkerReports()
    {
        return workerReports.getAll();
    }
    
    public synchronized List<WorkerReport> getAllWorkerReports(int workerID)
    {
        return workerReports.getAll(workerID);
    }
    
    public synchronized void save(WorkerReport report)
    {
        workerReports.save(report);
    }
    
    public synchronized void save(MuleReport report)
    {
        muleReports.save(report);
    }
    
    public synchronized void save(TrekkingReport report)
    {
        trekkingReports.save(report);
    }
    
    
    public synchronized long getRuntime(int workerID)
    {
        return workerReports.getRuntime(workerID);
    }
    
    public synchronized long getRuntimeBefore(int workerID, ZonedDateTime time)
    {
        return workerReports.getRuntimeBefore(workerID, time);
    }
    
    public synchronized long getRuntimeAfter(int workerID, ZonedDateTime time)
    {
        return workerReports.getRuntimeAfter(workerID, time);
    }
    public synchronized int getJobsCompleted(int muleID)
    {
        return muleReports.getJobsCompleted(muleID);
    }
    
    public void close()
    {
        workerReports.close();
        muleReports.close();
        trekkingReports.close();
    }
}
