package core.database.tables;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Proxies")
@Table
public class Proxy
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String provider;
    String ipAddress;
    String username;
    String password;
    int socksPort;
    
    public Proxy(){}
    
    public Proxy(String provider, String ipAddress, String username, String password, int socksPort)
    {
        this.provider = provider;
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.socksPort = socksPort;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getProvider()
    {
        return provider;
    }
    
    public void setProvider(String provider)
    {
        this.provider = provider;
    }
    
    public String getIpAddress()
    {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public int getSocksPort()
    {
        return socksPort;
    }
    
    public void setSocksPort(int socksPort)
    {
        this.socksPort = socksPort;
    }

    @Override
    public String toString()
    {
        return provider + id;
    }
}
