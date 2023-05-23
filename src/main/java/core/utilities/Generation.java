package core.utilities;

import config.SharedConfig;
import core.database.Dictionary;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class Generation
{
    public static ZonedDateTime generateBirthday()
    {
        return generateTime(ZonedDateTime.now().minusYears(60), ZonedDateTime.now().minusYears(20));
    }
    
    public static ZonedDateTime generateStartTime()
    {
        return generateTime(ZonedDateTime.now().plusDays(7), ZonedDateTime.now().plusDays(14));
    }
    
    
    public static ZonedDateTime generateTime(ZonedDateTime earliestDate, ZonedDateTime latestDate)
    {
        long earliestMilli = earliestDate.toInstant().toEpochMilli();
        long latestMilli = latestDate.toInstant().toEpochMilli();
        long gap = latestMilli - earliestMilli;
        
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(earliestMilli + ThreadLocalRandom.current().nextLong(gap)), SharedConfig.TIMEZONE);
    }
    
    public static String generateSeedWord()
    {
        Dictionary dictionary = new Dictionary();
        return dictionary.getWord(ThreadLocalRandom.current().nextInt(Dictionary.WORD_COUNT));
    }
    
    
    
}
