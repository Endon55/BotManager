package core.utilities;

import config.SharedConfig;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utils
{
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss");
    
    public static String getPrettyTime(ZonedDateTime zonedDateTime)
    {
        return zonedDateTime.format(dateTimeFormatter);
    }
    
    
    public static void sleep(long ms)
    {
        try{
            Thread.sleep(ms);
        } catch(InterruptedException ignored)
        {
        
        }
    }
    public static Date localToDate(LocalDate date)
    {
        if(date == null) return null;
        return Date.from(date.atStartOfDay(SharedConfig.TIMEZONE).toInstant());
    }
    
    public static LocalDate dateToLocal(Date date)
    {
        if(date == null) return null;
        return LocalDate.from(date.toInstant().atZone(SharedConfig.TIMEZONE).toLocalDate());
    }
    
    public static LocalDate zonedToLocal(ZonedDateTime date)
    {
        if(date == null) return null;
        return LocalDate.from(date);
    }
    
    public static ZonedDateTime localToZoned(LocalDate date)
    {
        if(date == null) return null;
        return ZonedDateTime.of(date.atStartOfDay(), SharedConfig.TIMEZONE);
    }

}
