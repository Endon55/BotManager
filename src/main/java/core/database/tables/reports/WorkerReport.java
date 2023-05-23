package core.database.tables.reports;

import config.TimeConstants;
import core.database.tables.accounts.WorkerAccount;
import tools.utils.NullSafe;
import types.accounts.Task;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity(name = "Worker_Reports")
@Table
public class WorkerReport
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private ZonedDateTime startTime;
    
    @OneToOne
    @JoinColumn(name = "worker_id")
    private WorkerAccount worker;
    
    @Enumerated(EnumType.STRING)
    private Task task;
    
    private int expenses;
    private int profit;
    
    private ZonedDateTime endTime;
    
    
    public WorkerReport()
    {
    
    }
    
    public WorkerReport(ZonedDateTime startTime, WorkerAccount worker, int profit, int expenses, ZonedDateTime endTime)
    {
        this.startTime = startTime;
        this.worker = worker;
        this.task = worker.getTask();
        this.profit = profit;
        this.expenses = expenses;
        this.endTime = endTime;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public ZonedDateTime getStartTime()
    {
        return startTime;
    }
    
    public void setStartTime(ZonedDateTime startTime)
    {
        this.startTime = startTime;
    }
    
    public WorkerAccount getWorker()
    {
        return worker;
    }
    
    public void setWorker(WorkerAccount worker)
    {
        this.worker = worker;
    }
    
    public Task getTask()
    {
        return task;
    }
    
    public void setTask(Task task)
    {
        this.task = task;
    }
    
    public int getProfit()
    {
        return profit;
    }
    
    public void setProfit(int profit)
    {
        this.profit = profit;
    }
    
    public ZonedDateTime getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(ZonedDateTime endTime)
    {
        this.endTime = endTime;
    }
    

    
    public long getRuntimeMillis()
    {
        return endTime.toInstant().toEpochMilli() - startTime.toInstant().toEpochMilli();
    }
    
    @Override
    public String toString()
    {
        return "Report{" +
                "id=" + id +
                ", worker=" + NullSafe.of(worker).get(WorkerAccount::getDisplayName) +
                ", task=" + task +
                ", profit=" + profit +
                ", expenses=" + expenses +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", hours=" + (getRuntimeMillis() / TimeConstants.MILLISECONDS_IN_HOUR) +
                '}';
    }
}
