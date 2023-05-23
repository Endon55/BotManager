package core.appenders;

import core.gui.GUI;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.sql.Date;
import java.time.Instant;

@Plugin(name = "GuiAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class GuiConsoleAppender extends AbstractAppender
{
    protected GuiConsoleAppender(final String name, final Layout layout)
    {
        super(name, null, layout, false);
    }
    
    @PluginFactory
    public static GuiConsoleAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Layout") Layout layout)
    {
        return new GuiConsoleAppender(name, layout);
    }
    
    @Override
    public void append(LogEvent event)
    {
        GUI.appendToConsole("[ " + Date.from(Instant.ofEpochMilli(event.getTimeMillis())) + " ][ " + event.getLevel() + " ][ " + getLoggerName(event.getLoggerName()) + " ]" + event.getMessage().getFormattedMessage());

    }
    
    
    public String getLoggerName(String loggerFullName)
    {
        String[] strings = loggerFullName.split("[.]");
        if(strings.length > 1)
        {
            return strings[strings.length - 1];
        }
        return loggerFullName;
    }
    
    
}
