package core.clients;

import config.SharedConfig;
import config.TimeConstants;
import core.types.Shift;
import core.types.Timeframe;
import lombok.extern.log4j.Log4j2;
import types.TimingPolicy;
import types.accounts.Task;

import java.time.ZonedDateTime;
import java.util.Random;
@Log4j2
public class Schedule
{
    public static final double COMPOUND_DAY_MULTIPLIER = 1.3;
    TimingPolicy policy;
    Shift shift;
    Task task;
    int seed;
    int startOffset;
    int endOffset;
    long maxWorkingMillis;
    Timeframe workTimeframe;
    Timeframe offsetTimeframe;
    
    public Schedule(TimingPolicy policy, Shift shift, Task task)
    {
        this(policy, shift, task, new Random().nextInt());
    }
    
    public Schedule(TimingPolicy policy, Shift shift, Task task, int seed)
    {
        this.policy = policy;
        this.seed = seed;
        this.shift = shift;
        this.task = task;
        
        Random rand = new Random(seed);
        
        int policyTimeRange = policy.getTimeRangeMinutes();
        if(shift.getShiftLengthHours() < (policyTimeRange * 2) / 60)
        {
            policyTimeRange = (int)((shift.getShiftLengthHours() * 60) *.2f);
            log.warn("Shift is shorter than passed TimingPolicy. Calculating instants using  a flat 20% of shift length, new Value: "
                    + policyTimeRange + ", Policy: " + policy + "(" + policy.getTimeRangeMinutes() + "), Shift: "
                    + shift + "(start=" + shift.getStartHour() + ", duration=" + shift.getShiftLengthHours() + "hrs)");
        }
        
        
        startOffset = rand.nextInt(policyTimeRange);
        endOffset = rand.nextInt(policyTimeRange);
        
        long shiftSeconds = shift.getShiftLengthHours() * TimeConstants.SECONDS_IN_HOUR;
        long offsetShiftSeconds = shiftSeconds - ((long) startOffset * 60) - ((long) endOffset * 60);
    
        ZonedDateTime now = ZonedDateTime.now(SharedConfig.TIMEZONE);
    
        workTimeframe = new Timeframe(shift.getStartHour(), shiftSeconds);
        offsetTimeframe = new Timeframe(
                now.withHour(shift.getStartHour() + startOffset / 60).withMinute(startOffset % 60).withSecond(0).withNano(0),
                offsetShiftSeconds);
        //log.info("Work Timeframe: " + workTimeframe);
        //log.info("Offset Timeframe: " + offsetTimeframe);
        if(workTimeframe.after(now))
        {
            log.info("Re-Generating profile for tomorrow.");
            workTimeframe = new Timeframe(workTimeframe.getStart().plusDays(1), shiftSeconds);
            offsetTimeframe = new Timeframe(offsetTimeframe.getStart().plusDays(1), offsetShiftSeconds);
        }

    }

    public int getSeed()
    {
        return seed;
    }
    
    public int getStartOffset()
    {
        return startOffset;
    }
    
    public int getEndOffset()
    {
        return endOffset;
    }
    

    /**
     * @return the unaltered timeframe generated from shift hours
     */
    public Timeframe getWorkTimeframe()
    {
        return workTimeframe;
    }
    
    /**
     * @return the timeframe altered by a randomized start and end time
     */
    public Timeframe getOffsetTimeframe()
    {
        return offsetTimeframe;
    }
    
    @Override
    public String toString()
    {
        return "ScheduleProfile{" +
                "offsetTimeframe=" + offsetTimeframe +
                ", startOffsetMin=" + startOffset +
                ", endOffsetMin=" + endOffset +
                '}';
    }
}
