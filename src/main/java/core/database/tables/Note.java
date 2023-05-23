package core.database.tables;

import core.database.tables.accounts.Account;
import types.errors.Flag;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity(name = "Notes")
public class Note
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    @ManyToOne
    private Account account;
    
    @Enumerated(EnumType.STRING)
    private Flag flag;
    private String details;
    
    public Note()
    {}
    
    public Note(Account account, Flag flag, String details)
    {
        this.account = account;
        this.flag = flag;
        this.details = details;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public Account getAccount()
    {
        return account;
    }
    
    public void setAccount(Account account)
    {
        this.account = account;
    }
    
    public Flag getFlag()
    {
        return flag;
    }
    
    public void setFlag(Flag flag)
    {
        this.flag = flag;
    }
    
    public String getDetails()
    {
        return details;
    }
    
    public void setDetails(String details)
    {
        this.details = details;
    }
    
    @Override
    public String toString()
    {
        return "Note{" +
                "flag=" + flag +
                ", details='" + details + '\'' +
                '}';
    }
}
