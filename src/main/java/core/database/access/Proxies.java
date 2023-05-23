package core.database.access;

import core.database.tables.Proxy;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class Proxies extends Dao<Proxy, Integer>
{
    public Proxies(SessionFactory sessionFactory)
    {
        super(sessionFactory, Proxy.class, "Proxies");
    }
    
    public synchronized Optional<Proxy> get(String ipAddress)
    {
        Session session = getSession();
        List<Proxy> proxies = createQuery(session, "where IPADDRESS='" + ipAddress + "'").getResultList();
        session.close();
        if(proxies.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.ofNullable(proxies.get(0));
    }
    
}
