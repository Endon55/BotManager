package core.database.tables.items;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "Rs_Items")
public class RsItem
{
    @Id
    private int id;
    
    private String name;
    
    public RsItem() {}
    
    public RsItem(int id, String name)
    {
        this.id = id;
        this.name = name;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
