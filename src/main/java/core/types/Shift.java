package core.types;

import java.time.ZonedDateTime;
import java.util.Random;

public enum Shift
{
    MORNING_SHIFT("Morning", 0, 16),       //Midnight -> 4pm
    DAY_SHIFT("Day", 6, 16),               //6am -> 10pm
    EVENING_SHIFT("Evening", 12, 16),      //Noon -> 4am
    NIGHT_SHIFT("Night", 18, 16),          //6pm -> 10am

    ;
    
    final String shift;
    final int startHour;
    final int endHour;
    final int shiftLengthHours;
    final boolean overnight;
    
    Shift(String shift, int startHour, int shiftLengthHours)
    {
        this.shift = shift;
        this.startHour = startHour;
        
        //% 24 because a shift can wrap around to the next day.
        this.endHour = (startHour + shiftLengthHours) % 24;
        this.overnight = endHour < startHour;
        this.shiftLengthHours = shiftLengthHours;
    }
    
    
    public int getStartHour()
    {
        return startHour;
    }
    
    public int getEndHour()
    {
        return endHour;
    }
    
    
    public int getShiftLengthHours()
    {
        return shiftLengthHours;
    }
    
    
    public boolean isWithinShift()
    {
        return isWithinShift(ZonedDateTime.now().getHour());
    }
    
    public boolean isWithinShift(int hourToCheck)
    {
        if(isOvernight())
        {
            return hourToCheck >= startHour || hourToCheck < endHour;
        }
        else //This is a standard day shift
        {
            return hourToCheck >= startHour && hourToCheck < endHour;
        }
    }
    /**
     *
     * @return the shift starts one day and ends the next.
     */
    public boolean isOvernight()
    {
        return overnight;
    }
    
    public static Shift getRandom()
    {
        Random random = new Random();
        return Shift.values()[random.nextInt(Shift.values().length)];
    }
}