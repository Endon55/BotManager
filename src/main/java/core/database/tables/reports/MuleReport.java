package core.database.tables.reports;

import core.database.tables.accounts.Account;
import core.database.tables.accounts.MuleAccount;
import core.database.tables.items.TradeItem;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Mule_Reports")
@Table
public class MuleReport
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private ZonedDateTime startTime;
    
    @OneToOne
    @JoinColumn(name = "primary_id")
    private Account primary;
    
    @OneToOne
    @JoinColumn(name = "mule_id")
    private MuleAccount mule;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "report")
    private Set<TradeItem> muleItems = new HashSet<>();
    
    private ZonedDateTime endTime;
    
    
    public MuleReport() { }
    
    public MuleReport(Account primary, MuleAccount mule, Set<TradeItem> muleItems, ZonedDateTime startTime, ZonedDateTime endTime)
    {
        this.startTime = startTime;
        this.primary = primary;
        this.mule = mule;
        this.muleItems = muleItems;
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
    
    public Account getPrimary()
    {
        return primary;
    }
    
    public void setPrimary(Account primary)
    {
        this.primary = primary;
    }
    
    public MuleAccount getMule()
    {
        return mule;
    }
    
    public void setMule(MuleAccount mule)
    {
        this.mule = mule;
    }
    
    public ZonedDateTime getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(ZonedDateTime endTime)
    {
        this.endTime = endTime;
    }
    
    public Set<TradeItem> getMuleItems()
    {
        return muleItems;
    }
    
    public void setMuleItems(Set<TradeItem> muleItems)
    {
        this.muleItems = muleItems;
    }
    
    public long getRuntimeMillis()
    {
        return endTime.toInstant().toEpochMilli() - startTime.toInstant().toEpochMilli();
    }
    
    
    @Override
    public String toString()
    {
        return "MuleReport{" +
                "id=" + id +
                ", primary=" + primary +
                ", mule=" + mule +
                ", muleItems=" + muleItems +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
