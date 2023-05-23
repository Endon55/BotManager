package core.database.access.items;

import core.database.tables.items.RsItem;
import core.database.tables.items.TradeItem;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class Items
{
    RsItems rsItems;
    TradeItems tradeItems;
    public Items(SessionFactory sessionFactory)
    {
        this.rsItems = new RsItems(sessionFactory);
        this.tradeItems = new TradeItems(sessionFactory);
    }
    
    
    
    public synchronized void add(TradeItem tradeItem)
    {
        tradeItems.save(tradeItem);
    }
    
    public synchronized RsItem addOrGet(int rsId, String itemName)
    {
        Optional<RsItem> rsItem = rsItems.get(rsId);
        if(rsItem.isPresent())
        {
            return rsItem.get();
        }
        rsItems.save(new RsItem(rsId, itemName));
        rsItem = rsItems.get(rsId);
        return rsItem.get();
    }
}
