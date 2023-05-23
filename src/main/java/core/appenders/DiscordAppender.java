package core.appenders;

import core.messaging.Discord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import tools.utils.NullSafe;

//Core.CATEGORY_NAME  Appender.ELEMENT_TYPE
@Plugin(name="DiscordAppender", category=Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class DiscordAppender extends AbstractAppender
{
    
    
    protected DiscordAppender(final String name, final Layout layout)
    {
        super(name, null, layout, false);
    }
    
    @PluginFactory
    public static DiscordAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Layout") Layout layout)
    {
        return new DiscordAppender(name, layout);
    }
    
    
    
    @Override
    public void append(LogEvent event)
    {
        
        if(event.getLevel().isMoreSpecificThan(Level.ERROR))
        {
            Discord.post("[Core Critical Error]", "[" + event.getLoggerName() + "]" + event.getMessage()
                    .getFormattedMessage() + " -" + (NullSafe.of(event.getThrown()).isNull() ? "" : event.getThrown()), false);
        }
    }
   
}
