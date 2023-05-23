package core.database.tables.reports;


import core.database.tables.accounts.WorkerAccount;
import data.TrekkingData;
import types.game.CombatStyle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name = "Trekking_Reports")
@Table
public class TrekkingReport
{
    /*    public String[] trekkEventNames = new String[]
            {       //    0,          1,    ,2,      3,         4,        5,        6,       7,        8,             9,           10,              11,            12
                    "Out of Trek", "Shades", "Tentacles", "Vampyres", "Ghasts", "Snails", "Snakes", "Bog", "Log Bridge", "Vine Swing", "Abidor Crank", "Plank Bridge", "Nail Beasts"
            }; //This is cumulative
    */
    
    public TrekkingReport()
    {
    }
    
    public TrekkingReport(WorkerAccount worker, TrekkingData data)
    {
        setWorker(worker);
        
        setCombatStyle(data.combatStyle);
        setCombatStyleLevel(data.combatStyleLevel);

        setTrekkCount(data.trekkCount);
        setEventsCount(data.completeTrekkEventCount);
        
        setOutOfTrekTime(data.trekkEventDuration[0]);
        setInTrekTime(data.trekkDuration);
        
        setShadesCount(data.trekkEventCounts[1]);
        setShadesTime(data.trekkEventDuration[1]);
        
        setTentaclesCount(data.trekkEventCounts[2]);
        setTentaclesTime(data.trekkEventDuration[2]);
        
        setVampyresCount(data.trekkEventCounts[3]);
        setVampyresTime(data.trekkEventDuration[3]);
        
        setGhastsCount(data.trekkEventCounts[4]);
        setGhastsTime(data.trekkEventDuration[4]);
        
        setSnailsCount(data.trekkEventCounts[5]);
        setSnailsTime(data.trekkEventDuration[5]);
        
        setSnakesCount(data.trekkEventCounts[6]);
        setSnakesTime(data.trekkEventDuration[6]);
        
        setBogCount(data.trekkEventCounts[7]);
        setBogTime(data.trekkEventDuration[7]);
        
        setLogBridgeCount(data.trekkEventCounts[8]);
        setLogBridgeTime(data.trekkEventDuration[8]);
        
        setVineSwingCount(data.trekkEventCounts[9]);
        setVineSwingTime(data.trekkEventDuration[9]);
        
        setAbidorCrankCount(data.trekkEventCounts[10]);
        setAbidorCrankTime(data.trekkEventDuration[10]);
        
        setPlankBridgeCount(data.trekkEventCounts[11]);
        setPlankBridgeTime(data.trekkEventDuration[11]);
        
        setNailBeastsCount(data.trekkEventCounts[12]);
        setNailBeastsTime(data.trekkEventDuration[12]);
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    @OneToOne
    @JoinColumn(name = "worker_id")
    private WorkerAccount worker;
    
    private CombatStyle combatStyle;
    private int combatStyleLevel;
    
    private int outOfTrekTime;
    private int inTrekTime;
    
    private int trekkCount;
    private int eventsCount;
    
    private int shadesCount;
    private int shadesTime;
    
    private int tentaclesCount;
    private int tentaclesTime;
    
    private int vampyresCount;
    private int vampyresTime;
    
    private int ghastsCount;
    private int ghastsTime;
    
    private int snailsCount;
    private int snailsTime;
    
    private int snakesCount;
    private int snakesTime;
    
    private int bogCount;
    private int bogTime;
    
    private int logBridgeCount;
    private int logBridgeTime;
    
    private int vineSwingCount;
    private int vineSwingTime;
    
    private int abidorCrankCount;
    private int abidorCrankTime;
    
    private int plankBridgeCount;
    private int plankBridgeTime;
    
    private int nailBeastsCount;
    private int nailBeastsTime;
    
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public WorkerAccount getWorker()
    {
        return worker;
    }
    
    public void setWorker(WorkerAccount worker)
    {
        this.worker = worker;
    }
    
    public CombatStyle getCombatStyle()
    {
        return combatStyle;
    }
    
    public void setCombatStyle(CombatStyle combatStyle)
    {
        this.combatStyle = combatStyle;
    }
    
    public int getCombatStyleLevel()
    {
        return combatStyleLevel;
    }
    
    public void setCombatStyleLevel(int combatStyleLevel)
    {
        this.combatStyleLevel = combatStyleLevel;
    }
    
    public int getOutOfTrekTime()
    {
        return outOfTrekTime;
    }
    
    public void setOutOfTrekTime(int outOfTrekTime)
    {
        this.outOfTrekTime = outOfTrekTime;
    }
    
    public int getInTrekTime()
    {
        return inTrekTime;
    }
    
    public void setInTrekTime(int inTrekTime)
    {
        this.inTrekTime = inTrekTime;
    }
    
    public int getTrekkCount()
    {
        return trekkCount;
    }
    
    public void setTrekkCount(int trekkCount)
    {
        this.trekkCount = trekkCount;
    }
    
    public int getEventsCount()
    {
        return eventsCount;
    }
    
    public void setEventsCount(int eventsCount)
    {
        this.eventsCount = eventsCount;
    }
    
    public int getShadesCount()
    {
        return shadesCount;
    }

    public void setShadesCount(int shadesCount)
    {
        this.shadesCount = shadesCount;
    }

    public int getShadesTime()
    {
        return shadesTime;
    }
    
    public void setShadesTime(int shadesTime)
    {
        this.shadesTime = shadesTime;
    }
    
    public int getAvgShadesTime()
    {
        return getShadesTime() / getShadesCount();
    }
    
    public int getTentaclesCount()
    {
        return tentaclesCount;
    }
    
    public void setTentaclesCount(int tentaclesCount)
    {
        this.tentaclesCount = tentaclesCount;
    }
    
    public int getTentaclesTime()
    {
        return tentaclesTime;
    }
    
    public void setTentaclesTime(int tentaclesTime)
    {
        this.tentaclesTime = tentaclesTime;
    }
    
    public int getAvgTentaclesTime()
    {
        return getTentaclesTime() / getTentaclesCount();
    }
    
    public int getVampyresCount()
    {
        return vampyresCount;
    }
    
    public void setVampyresCount(int vampyresCount)
    {
        this.vampyresCount = vampyresCount;
    }
    
    public int getVampyresTime()
    {
        return vampyresTime;
    }
    
    public void setVampyresTime(int vampyresTime)
    {
        this.vampyresTime = vampyresTime;
    }
    
    public int getAvgVampyresTime()
    {
        return getVampyresTime() / getVampyresCount();
    }
    
    public int getGhastsCount()
    {
        return ghastsCount;
    }
    
    public void setGhastsCount(int ghastsCount)
    {
        this.ghastsCount = ghastsCount;
    }
    
    public int getGhastsTime()
    {
        return ghastsTime;
    }
    
    public void setGhastsTime(int ghastsTime)
    {
        this.ghastsTime = ghastsTime;
    }
    
    public int getAvgGhastsTime()
    {
        return getGhastsTime() / getGhastsCount();
    }
    
    public int getSnailsCount()
    {
        return snailsCount;
    }
    
    public void setSnailsCount(int snailsCount)
    {
        this.snailsCount = snailsCount;
    }
    
    public int getSnailsTime()
    {
        return snailsTime;
    }
    
    public void setSnailsTime(int snailsTime)
    {
        this.snailsTime = snailsTime;
    }
    
    public int getAvgSnailsTime()
    {
        return getSnailsTime() / getSnailsCount();
    }
    
    public int getSnakesCount()
    {
        return snakesCount;
    }
    
    public void setSnakesCount(int snakesCount)
    {
        this.snakesCount = snakesCount;
    }
    
    public int getSnakesTime()
    {
        return snakesTime;
    }
    
    public void setSnakesTime(int snakesTime)
    {
        this.snakesTime = snakesTime;
    }
    
    public int getAvgSnakesTime()
    {
        return getSnakesTime() / getSnakesCount();
    }
    
    public int getBogCount()
    {
        return bogCount;
    }
    
    public void setBogCount(int bogCount)
    {
        this.bogCount = bogCount;
    }
    
    public int getBogTime()
    {
        return bogTime;
    }
    
    public void setBogTime(int bogTime)
    {
        this.bogTime = bogTime;
    }
    
    public int getAvgBogTime()
    {
        return getBogTime() / getBogCount();
    }
    
    public int getLogBridgeCount()
    {
        return logBridgeCount;
    }
    
    public void setLogBridgeCount(int logBridgeCount)
    {
        this.logBridgeCount = logBridgeCount;
    }
    
    public int getLogBridgeTime()
    {
        return logBridgeTime;
    }
    
    public void setLogBridgeTime(int logBridgeTime)
    {
        this.logBridgeTime = logBridgeTime;
    }
    
    public int getAvgLogBridgeTime()
    {
        return getLogBridgeTime() / getLogBridgeCount();
    }
    
    public int getVineSwingCount()
    {
        return vineSwingCount;
    }
    
    public void setVineSwingCount(int vineSwingCount)
    {
        this.vineSwingCount = vineSwingCount;
    }
    
    public int getVineSwingTime()
    {
        return vineSwingTime;
    }
    
    public void setVineSwingTime(int vineSwingTime)
    {
        this.vineSwingTime = vineSwingTime;
    }
    
    public int getAvgVineSwingTime()
    {
        return getVineSwingTime() / getVineSwingCount();
    }
    
    public int getAbidorCrankCount()
    {
        return abidorCrankCount;
    }
    
    public void setAbidorCrankCount(int abidorCrankCount)
    {
        this.abidorCrankCount = abidorCrankCount;
    }
    
    public int getAbidorCrankTime()
    {
        return abidorCrankTime;
    }
    
    public void setAbidorCrankTime(int abidorCrankTime)
    {
        this.abidorCrankTime = abidorCrankTime;
    }
    
    public int getAvgAbidorCrankTime()
    {
        return getAbidorCrankTime() / getAbidorCrankCount();
    }
    
    public int getPlankBridgeCount()
    {
        return plankBridgeCount;
    }
    
    public void setPlankBridgeCount(int plankBridgeCount)
    {
        this.plankBridgeCount = plankBridgeCount;
    }
    
    public int getPlankBridgeTime()
    {
        return plankBridgeTime;
    }
    
    public void setPlankBridgeTime(int plankBridgeTime)
    {
        this.plankBridgeTime = plankBridgeTime;
    }
    
    public int getAvgPlankBridgeTime()
    {
        return getPlankBridgeTime() / getPlankBridgeCount();
    }
    
    public int getNailBeastsCount()
    {
        return nailBeastsCount;
    }
    
    public void setNailBeastsCount(int nailBeastsCount)
    {
        this.nailBeastsCount = nailBeastsCount;
    }
    
    public int getNailBeastsTime()
    {
        return nailBeastsTime;
    }
    
    public void setNailBeastsTime(int nailBeastsTime)
    {
        this.nailBeastsTime = nailBeastsTime;
    }
    
    public int getAvgNailBeastsTime()
    {
        return getNailBeastsTime() / getNailBeastsCount();
    }
}
