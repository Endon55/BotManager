package core.database.access.items;

import core.database.access.Dao;
import core.database.tables.items.TradeItem;
import org.hibernate.SessionFactory;

public class TradeItems extends Dao<TradeItem, Integer>
{
    public TradeItems(SessionFactory sessionFactory)
    {
        super(sessionFactory, TradeItem.class, "Trade_Items");
    }
    
}
