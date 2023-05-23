package core.database.tables.items;

import core.database.tables.reports.MuleReport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity(name = "Trade_Items")
public class TradeItem
{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    @ManyToOne
    private MuleReport report;
    
    @ManyToOne
    @JoinColumn(name = "rs_item_id")
    private RsItem rsItem;
    
    private int quantity;
    
    
    public TradeItem() { }
    
    public TradeItem(MuleReport report, RsItem rsItem, int quantity)
    {
        this.report = report;
        this.rsItem = rsItem;
        this.quantity = quantity;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public MuleReport getReport()
    {
        return report;
    }
    
    public void setReport(MuleReport report)
    {
        this.report = report;
    }
    
    public RsItem getRsItem()
    {
        return rsItem;
    }
    
    public void setRsItem(RsItem rsItem)
    {
        this.rsItem = rsItem;
    }
    
    public int getQuantity()
    {
        return quantity;
    }
    
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }
    
    public int getRsId()
    {
        return getRsItem().getId();
    }
    
    public String getName()
    {
        return getRsItem().getName();
    }
    
}
