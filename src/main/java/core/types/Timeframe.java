package core.types;

import config.SharedConfig;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class Timeframe
{
    private final ZonedDateTime start;
    private final ZonedDateTime end;
    
    public Timeframe(ZonedDateTime start, ZonedDateTime end)
    {
        this.start = start;
        this.end = end;
    }
    
    public Timeframe(ZonedDateTime start, long durationSeconds)
    {
        this.start = start;
        this.end = start.plusSeconds(durationSeconds);
    }
    
    public Timeframe(int atHour, long durationSeconds)
    {
        start = ZonedDateTime.now(SharedConfig.TIMEZONE).truncatedTo(ChronoUnit.DAYS).withHour(atHour);
        this.end = start.plusSeconds(durationSeconds);
    }
    
    public boolean within()
    {
        ZonedDateTime timeToCheck = ZonedDateTime.now(SharedConfig.TIMEZONE);
        return timeToCheck.isAfter(start) && timeToCheck.isBefore(end);
    }
    public boolean within(ZonedDateTime timeToCheck)
    {
        return timeToCheck.isAfter(start) && timeToCheck.isBefore(end);
    }
    public boolean before()
    {
        ZonedDateTime timeToCheck = ZonedDateTime.now(SharedConfig.TIMEZONE);
        return timeToCheck.isBefore(start);
    }
    public boolean before(ZonedDateTime timeToCheck)
    {
        return timeToCheck.isBefore(start);
    }
    
    public boolean after()
    {
        ZonedDateTime timeToCheck = ZonedDateTime.now(SharedConfig.TIMEZONE);
        return timeToCheck.isAfter(end);
    }
    
    public boolean after(ZonedDateTime timeToCheck)
    {
        return timeToCheck.isAfter(end);
    }
    
    
    public ZonedDateTime getStart()
    {
        return start;
    }
    
    public ZonedDateTime getEnd()
    {
        return end;
    }
    
    
    @Override
    public String toString()
    {
        return "Timeframe{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
