package core.database.tables.accounts;

import core.database.tables.Note;
import core.database.tables.Proxy;
import core.types.Shift;
import types.accounts.Personality;
import types.accounts.Task;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity(name = "Worker")
public class WorkerAccount extends Account
{
    @Enumerated(EnumType.STRING)
    private Task task;

    @Enumerated(EnumType.STRING)
    private Shift shift;
    
    @Enumerated(EnumType.STRING)
    private Personality personality;
    
    @ManyToOne
    private MuleAccount mule;
    
    public WorkerAccount() {}
    
    public WorkerAccount(String displayName, String email, String password, int bankPin, int gameTime, int cash, ZonedDateTime creationDate, ZonedDateTime birthday, ZonedDateTime startTime, Proxy proxy, Set<Note> notes, Task task, Shift shift, Personality personality, MuleAccount mule)
    {
        super(displayName, email, password, bankPin, gameTime, cash, creationDate, birthday, startTime, proxy, notes);
        this.task = task;
        this.shift = shift;
        this.personality = personality;
        this.mule = mule;
    }
    
    public Task getTask()
    {
        return task;
    }
    
    public void setTask(Task task)
    {
        this.task = task;
    }

    public Shift getShift()
    {
        return shift;
    }
    
    public void setShift(Shift shift)
    {
        this.shift = shift;
    }
    
    public Personality getPersonality()
    {
        return personality;
    }
    
    public void setPersonality(Personality personality)
    {
        this.personality = personality;
    }

    public MuleAccount getMule()
    {
        return mule;
    }
    
    public void setMule(MuleAccount mule)
    {
        this.mule = mule;
    }
    
    @Override
    public String toString()
    {
        return "WorkerAccount{" +
                "id=" + getId() +
                ", displayName='" + getDisplayName() + '\'' +
                '}';
        /*+
                ", email='" + getEmail() + '\'' +
                ", gameTime=" + getGameTime() +
                ", cash=" + getCash() +
                ", creationDate=" + getCreationDate() +
                ", birthday=" + getBirthday() +
                ", startTime=" + getStartTime() +
                ", proxy=" + getProxy() +
                ", task=" + task +
                ", shift=" + shift +
                ", personality=" + personality +
                ", mule=" + mule +
                ", notes=" + getNotes() +*/
                
    }
}
