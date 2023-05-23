package core.database;

import core.database.access.Proxies;
import core.database.access.accounts.AccountProvisioner;
import core.database.access.items.Items;
import core.database.access.reports.Reports;
import core.database.tables.Note;
import core.database.tables.Proxy;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.BannedAccount;
import core.database.tables.accounts.FreshAccount;
import core.database.tables.accounts.MuleAccount;
import core.database.tables.accounts.WorkerAccount;
import core.database.tables.items.RsItem;
import core.database.tables.items.TradeItem;
import core.database.tables.reports.MuleReport;
import core.database.tables.reports.TrekkingReport;
import core.database.tables.reports.WorkerReport;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class Database
{
    private final SessionFactory sessionFactory;
    
    private AccountProvisioner accounts;
    private Items items;
    private Reports reports;
    private Proxies proxies;
    
    public Database()
    {
        Configuration configuration = new Configuration().configure();
        
        configuration.addAnnotatedClass(Account.class);
        configuration.addAnnotatedClass(MuleAccount.class);
        configuration.addAnnotatedClass(WorkerAccount.class);
        configuration.addAnnotatedClass(FreshAccount.class);
        configuration.addAnnotatedClass(BannedAccount.class);
        configuration.addAnnotatedClass(Proxy.class);
        configuration.addAnnotatedClass(Note.class);
        
        configuration.addAnnotatedClass(WorkerReport.class);
        configuration.addAnnotatedClass(MuleReport.class);
        configuration.addAnnotatedClass(TrekkingReport.class);
        
        configuration.addAnnotatedClass(RsItem.class);
        configuration.addAnnotatedClass(TradeItem.class);
        
        
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        this.sessionFactory = configuration.buildSessionFactory(builder.build());
        reports = new Reports(sessionFactory);
        proxies = new Proxies(sessionFactory);
        accounts = new AccountProvisioner(sessionFactory, reports, proxies);
        items = new Items(sessionFactory);
    }
    
    public synchronized AccountProvisioner getAccountProvisioner()
    {
        return accounts;
    }

    public synchronized Proxies getProxies()
    {
        return proxies;
    }
    
    public synchronized Reports getReports()
    {
        return reports;
    }
 
    public synchronized Items getItems()
    {
        return items;
    }
    
    
    public void close()
    {
        accounts.close();
        proxies.close();
        reports.close();
    }
}
