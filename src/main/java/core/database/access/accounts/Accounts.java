package core.database.access.accounts;

import core.database.access.Dao;
import core.database.tables.accounts.Account;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
@Log4j2
public class Accounts extends Dao<Account, Integer>
{

    public Accounts(SessionFactory sessionFactory)
    {
        super(sessionFactory, Account.class, "Accounts");
    }
    
    public synchronized Optional<Account> get(String displayName)
    {
        Session session = getSession();
        List<Account> accounts = createQuery(session, "where DISPLAYNAME='" + displayName + "'").getResultList();
        session.close();
        if(accounts.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.ofNullable(accounts.get(0));
    }
    
    public synchronized void convertToFresh(int accountID)
    {
        convertTo(accountID, "Fresh");
    }
    public synchronized void convertToWorker(int accountID)
    {
        convertTo(accountID, "Worker");
    }
    
    public synchronized void convertToMule(int accountID)
    {
        convertTo(accountID, "Mule");
    }
    
    public synchronized void convertToBanned(int accountID)
    {
        convertTo(accountID, "Banned");
    }

    
    private synchronized void convertTo(int accountID, String dType)
    {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        String convertStr = "update ACCOUNTS set DTYPE = '" + dType + "' where ID=" + accountID;
        session.createSQLQuery(convertStr).executeUpdate();
        transaction.commit();
        session.close();
    }
    
    public synchronized void setGameTime(int accountID, int gameTime)
    {
        Optional<Account> account = get(accountID);
        if(account.isPresent())
        {
            account.get().setGameTime(gameTime);
            update(account.get());
        }
        else{
            log.info("Couldn't find account(" + accountID + ") to set game time: " + gameTime);
        }
    }
    
    /**
     * @return How much cash was gained or lost. NewCash - OldCash;
     */
    public synchronized int setCash(int accountID, int cash)
    {
        Optional<Account> account = get(accountID);
        if(account.isPresent())
        {
            int oldCash = account.get().getCash();
            account.get().setCash(cash);
            update(account.get());
            return cash - oldCash;
        }
        else
        {
            log.info("Couldn't find account(" + accountID + ") to set cash: " + cash);
        }
        return 0;
    }

    public synchronized void setStartTime(Account account, ZonedDateTime startTime)
    {
        account.setStartTime(startTime);
        update(account);
    }
    public synchronized void setStartTime(int accountID, ZonedDateTime startTime)
    {
        Optional<Account> account = get(accountID);
        if(account.isPresent())
        {
            setStartTime(account.get(), startTime);
        }
        else
        {
            log.info("Couldn't find account(" + accountID + ") to set game time.");
        }
    }
}
