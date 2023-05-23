package core.database.tables.accounts;

import org.hibernate.annotations.ColumnDefault;
import types.accounts.Task;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Entity(name = "Banned")
public class BannedAccount extends Account
{
    @Enumerated(EnumType.STRING)
    private Task task;

    /**
     * For mules, runtime is the number of mule jobs they completed. For workers, it's how many ms they spent working.
     */
    @ColumnDefault("0")
    private long runtime = 0;
    
    private Date banDate;
    
    public Task getTask()
    {
        return task;
    }
    
    public void setTask(Task task)
    {
        this.task = task;
    }
    
    public long getRuntime()
    {
        return runtime;
    }
    
    public void setRuntime(long runtime)
    {
        this.runtime = runtime;
    }
    
    public Date getBanDate()
    {
        return banDate;
    }
    
    public void setBanDate(Date banDate)
    {
        this.banDate = banDate;
    }

    @Override
    public String toString()
    {
        return "BannedAccount{" +
                "id=" + getId() +
                ", displayName='" + getDisplayName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", gameTime=" + getGameTime() +
                ", cash=" + getCash() +
                ", creationDate=" + getCreationDate() +
                ", birthday=" + getBirthday() +
                ", startTime=" + getStartTime() +
                ", proxy=" + getProxy() +
                ", task=" + task +
                ", runtime=" + runtime +
                ", banDate=" + banDate +
                ", notes=" + getNotes() +
                '}';
    }
}
