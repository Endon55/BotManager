package core.database.tables.accounts;


import core.database.tables.Note;
import core.database.tables.Proxy;
import org.hibernate.annotations.ColumnDefault;
import types.errors.Flag;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "Accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Account
{
    
    public Account() {}
    
    public Account(String displayName, String email, String password, int bankPin, int gameTime, int cash, ZonedDateTime creationDate, ZonedDateTime birthday, ZonedDateTime startTime, Proxy proxy, Set<Note> notes)
    {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.bankPin = bankPin;
        this.gameTime = gameTime;
        this.cash = cash;
        this.creationDate = creationDate;
        this.birthday = birthday;
        this.startTime = startTime;
        this.proxy = proxy;
        this.notes = notes;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String displayName;
    private String email;
    private String password;
    
    @ColumnDefault("-1")
    private int bankPin = -1;
    
    @ColumnDefault("0")
    private int gameTime = 0;
    
    @ColumnDefault("0")
    private int cash = 0;
    
    private ZonedDateTime creationDate;
    private ZonedDateTime birthday;
    
    private ZonedDateTime startTime;
    
    @OneToOne(fetch = FetchType.EAGER)
    private Proxy proxy;
    
    @OneToMany(fetch=FetchType.EAGER, mappedBy = "account")
    private Set<Note> notes = new HashSet<>();
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getDisplayName()
    {
        return displayName;
    }
    
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public int getBankPin()
    {
        return bankPin;
    }
    
    public void setBankPin(int bankPin)
    {
        this.bankPin = bankPin;
    }
    
    public int getGameTime()
    {
        return gameTime;
    }
    
    public void setGameTime(int gameTime)
    {
        this.gameTime = gameTime;
    }
    
    public int getCash()
    {
        return cash;
    }
    
    public void setCash(int cash)
    {
        this.cash = cash;
    }
    
    public ZonedDateTime getCreationDate()
    {
        return creationDate;
    }
    
    public void setCreationDate(ZonedDateTime creationDate)
    {
        this.creationDate = creationDate;
    }
    
    public ZonedDateTime getBirthday()
    {
        return birthday;
    }
    
    public void setBirthday(ZonedDateTime birthday)
    {
        this.birthday = birthday;
    }
    
    public ZonedDateTime getStartTime()
    {
        return startTime;
    }
    
    public void setStartTime(ZonedDateTime startTime)
    {
        this.startTime = startTime;
    }
    
    public Set<Note> getNotes()
    {
        return notes;
    }
    
    public void setNotes(Set<Note> notes)
    {
        this.notes = notes;
    }

    public Proxy getProxy()
    {
        return proxy;
    }
    
    public void setProxy(Proxy proxy)
    {
        this.proxy = proxy;
    }
    
    public boolean hasNote(Flag flag)
    {
        for(Note note :
                getNotes())
        {
            if(note.getFlag().equals(flag))
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasNotes(Flag...flags)
    {
        List<Flag> flagList = new ArrayList<>(Arrays.asList(flags));
        for(Note note :
                getNotes())
        {
            if(flagList.contains(note.getFlag()))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        return "Account{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
/*                ", gameTime=" + gameTime +
                ", cash=" + cash +
                ", creationDate=" + creationDate +
                ", birthday=" + birthday +
                ", startTime=" + startTime +
                ", proxy=" + proxy +
                ", notes=" + notes +*/
                '}';
    }
}
