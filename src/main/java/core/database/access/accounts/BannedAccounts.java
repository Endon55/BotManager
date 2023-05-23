package core.database.access.accounts;

import core.database.access.Dao;
import core.database.tables.accounts.BannedAccount;
import org.hibernate.SessionFactory;

public class BannedAccounts extends Dao<BannedAccount, Integer>
{
    public BannedAccounts(SessionFactory sessionFactory)
    {
        super(sessionFactory, BannedAccount.class, "Banned");
    }
    
}
