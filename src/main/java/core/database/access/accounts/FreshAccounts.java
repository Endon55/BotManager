package core.database.access.accounts;

import core.database.access.Dao;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.FreshAccount;
import org.hibernate.SessionFactory;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FreshAccounts extends Dao<FreshAccount, Integer>
{
    public FreshAccounts(SessionFactory sessionFactory)
    {
        super(sessionFactory, FreshAccount.class, "Fresh");
    }
    
    public synchronized List<FreshAccount> getAgedAccounts()
    {
        return getAll().stream()
                .filter(freshAccount -> freshAccount.getStartTime().isBefore(ZonedDateTime.now()))
                .collect(Collectors.toList());
    }
    
    public synchronized Optional<FreshAccount> getAgedAccount()
    {
        return getAll().stream()
                .filter(freshAccount -> freshAccount.getStartTime().isBefore(ZonedDateTime.now()))
                .min(Comparator.comparing(Account::getCreationDate)); //earliest -> latest
    }
    
    public synchronized int getRemainingAgedAccounts()
    {
        return getAgedAccounts().size();
    }
    
}
