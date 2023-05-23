package core;


import config.SharedConfig;
import core.restricted.LoadAccounts;
import lombok.extern.log4j.Log4j2;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import java.io.IOException;
import java.util.TimeZone;

@Log4j2
public class Main
{
    static Core core;
    
    public static void main(String[] args)
    {
        TimeZone.setDefault(TimeZone.getTimeZone(SharedConfig.TIMEZONE));

        run();
        //test();
        
    }
    
    public static void test()
    {

    }

    public static void loadDatabase()
    {
        LoadAccounts loadAccounts = new LoadAccounts();
        loadAccounts.load();
    }
    
    public static void run()
    {
        try
        {
            core = new Core();
        } catch(IOException e)
        {
            throw new RuntimeException("The Core failed to initialize. Error: " + e.getMessage());
        }
        
        core.run();
    }
    
    public void mute()
    {
        for(Mixer.Info info :
                AudioSystem.getMixerInfo())
        {
            System.out.println("Info: " + info.toString());
            Mixer mixer = AudioSystem.getMixer(info);
            for(Line line :
                    mixer.getSourceLines())
            {
                System.out.println("Line: " + line.toString());
                BooleanControl control = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
                if(control != null)
                {
                    System.out.println("Muting: " + control);
                    control.setValue(true);
                }
                
            }
            
        }
    }
    
}