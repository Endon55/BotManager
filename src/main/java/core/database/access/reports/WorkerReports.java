package core.database.access.reports;

import core.database.access.Dao;
import core.database.tables.reports.WorkerReport;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Log4j2
public class WorkerReports extends Dao<WorkerReport, Integer>
{
    
    public WorkerReports(SessionFactory sessionFactory)
    {
        super(sessionFactory, WorkerReport.class, "Worker_Reports");
    }
    
    public List<WorkerReport> getFromSeconds(long seconds)
    {
        Session session = getSession();
        Date now = Date.from(Instant.now());
        Date start = Date.from(Instant.ofEpochSecond(Instant.now().getEpochSecond() - seconds));
        List<WorkerReport> reports =  createQuery(session, "where ENDTIME between :start and :end")
                .setParameter("start", start).setParameter("end", now).getResultList();
        session.close();
        return reports;
    }
    
    public List<WorkerReport> getAll(int workerID)
    {
        Session session = getSession();
        List<WorkerReport> reports = createQuery(session, "where WORKER_ID='" + workerID + "'").getResultList();
        session.close();
        return reports;
    }
    
    

    
    public long getRuntime(int workerID)
    {
        long runtime = 0;
        for(WorkerReport report : getAll(workerID))
        {
            runtime += report.getRuntimeMillis();
        }
        return runtime;
    }
    
    public long getRuntimeBefore(int workerID, ZonedDateTime time)
    {
        long runtime = 0;
        for(WorkerReport report : getAll(workerID))
        {
            if(report.getEndTime().isBefore(time))
            {
                runtime += report.getRuntimeMillis();
            }
        }
        return runtime;
    }
    public long getRuntimeAfter(int workerID, ZonedDateTime time)
    {
        long runtime = 0;
        for(WorkerReport report : getAll(workerID))
        {
            if(report.getStartTime().isAfter(time))
            {
                runtime += report.getRuntimeMillis();
            }
        }
        return runtime;
    }
}
