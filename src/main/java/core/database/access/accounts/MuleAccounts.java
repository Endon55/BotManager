package core.database.access.accounts;

import core.config.Config;
import core.database.access.Dao;
import core.database.tables.accounts.MuleAccount;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import types.errors.Flag;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MuleAccounts extends Dao<MuleAccount, Integer>
{
    public MuleAccounts(SessionFactory sessionFactory)
    {
        super(sessionFactory, MuleAccount.class, "Mule");
    }
    
    
    /**
     * @return how many more workers the collection of mules can hold given MAX_WORKERS_PER_MULE
     */
    public synchronized int getRemainingMuleCapacity()
    {
        return getAll().stream()
                .mapToInt(muleAccount -> Math.max(0, Config.MAX_WORKERS_PER_MULE - muleAccount.getWorkers().size()))
                .sum();
    }
    
    public synchronized Optional<MuleAccount> getFreeMule()
    {
        List<MuleAccount> freeMules = getFreeMules();
        if(freeMules.size() > 0) return Optional.ofNullable(freeMules.get(0));
        return Optional.empty();
    }
    
    public synchronized Optional<MuleAccount> get(String displayName)
    {
        Session session = getSession();
        Optional<MuleAccount> accountOpt = Optional.of(createQuery(session, "where DISPLAYNAME='" + displayName + "'").getSingleResult());
        session.close();
        return accountOpt;
    }
    /**
     * @return collection is sorted from lowest # of workers to highest.
     */
    public synchronized List<MuleAccount> getFreeMules()
    {
        return getAll().stream()
                .filter(muleAccount -> muleAccount.isStandard() && !muleAccount.hasNote(Flag.UNINITIALIZED) && !muleAccount.hasNote(Flag.TEMPORARY_BAN))
                //.filter(muleAccount -> muleAccount.getWorkers().size() < Config.MAX_WORKERS_PER_MULE)
                .sorted(Comparator.comparing(muleAccount -> muleAccount.getWorkers().size()))
                .collect(Collectors.toList());
    }

}
