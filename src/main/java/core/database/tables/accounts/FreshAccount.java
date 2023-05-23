package core.database.tables.accounts;

import core.database.tables.Note;
import core.database.tables.Proxy;

import javax.persistence.Entity;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity(name = "Fresh")
public class FreshAccount extends Account
{
    public FreshAccount()
    {
    }
    
    public FreshAccount(String displayName, String email, String password, int bankPin, int gameTime, int cash, ZonedDateTime creationDate, ZonedDateTime birthday, ZonedDateTime startTime, Proxy proxy, Set<Note> notes)
    {
        super(displayName, email, password, bankPin, gameTime, cash, creationDate, birthday, startTime, proxy, notes);
    }
    
}
