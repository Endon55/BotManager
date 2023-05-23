package core.clients.launcher;

public enum Layout
{
    NO_PREFERENCE("no_preference"),
    FIXED("fixed"),
    RESIZEABLE_CLASSIC("resizable_classic"),
    RESIZABLE_MODERN("resizable_modern"),
    ;
    
    
    
    final String argument;
    Layout(String argument)
    {
        this.argument = argument;
    }
    
    public String getArgument()
    {
        return argument;
    }
}
