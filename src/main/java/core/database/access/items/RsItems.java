package core.database.access.items;

import core.database.access.Dao;
import core.database.tables.items.RsItem;
import org.hibernate.SessionFactory;

public class RsItems extends Dao<RsItem, Integer>
{
    public RsItems(SessionFactory sessionFactory)
    {
        super(sessionFactory, RsItem.class, "Rs_Items");
    }
    
    
}
