package core.database.tables.accounts;


import core.database.tables.Note;
import core.database.tables.Proxy;
import types.accounts.MuleType;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Mule")
@Access(AccessType.FIELD)
public class MuleAccount extends Account
{

    
    @Enumerated(EnumType.STRING)
    private MuleType type;
    
    @OneToMany(fetch= FetchType.EAGER, mappedBy = "mule")
    private Set<WorkerAccount> workers = new HashSet<>();
    
    
    public MuleAccount() {}
    
    public MuleAccount(String displayName, String email, String password, int bankPin, int gameTime, int cash, ZonedDateTime creationDate, ZonedDateTime birthday, ZonedDateTime startTime, Proxy proxy, Set<Note> notes, Set<WorkerAccount> workers)
    {
        super(displayName, email, password, bankPin, gameTime, cash, creationDate, birthday, startTime, proxy, notes);
        this.workers = workers;
    }

    public Set<WorkerAccount> getWorkers()
    {
        return workers;
    }
    
    public void setWorkers(Set<WorkerAccount> workers)
    {
        this.workers = workers;
    }
    
    public void addWorker(WorkerAccount worker)
    {
        getWorkers().add(worker);
    }
    
    public MuleType getType()
    {
        return type;
    }
    
    public void setType(MuleType type)
    {
        this.type = type;
    }
    
    public boolean isStandard()
    {
        return getType() == MuleType.STANDARD;
    }
    
    public boolean isManual()
    {
        return getType() == MuleType.MANUAL;
    }
    
    public boolean isBurner()
    {
        return getType() == MuleType.BURNER;
    }
    
    @Override
    public String toString()
    {
        return "MuleAccount{" +
                "id=" + getId() +
                ", displayName='" + getDisplayName() + '\'' +
      /*          ", email='" + getEmail() + '\'' +
                ", gameTime=" + getGameTime() +
                ", cash=" + getCash() +
                ", creationDate=" + getCreationDate() +
                ", birthday=" + getBirthday() +
                ", startTime=" + getStartTime() +
                ", proxy=" + getProxy() +
                ", manual=" + manual +
                ", workers=" + workers.stream().map(WorkerAccount::getDisplayName).collect(Collectors.toList()) +
                ", notes=" + getNotes() +*/
                '}';
    }
}
