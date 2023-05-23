package core.clients.launcher;

public enum Paint
{
    ALL("all"), //Renders everything
    SCRIPT("script"), //Renders only script paint
    NONE("none"), //Renders only script paint
    ;
    
    
    final String argument;
    
    Paint(String argument)
    {
        this.argument = argument;
    }
    
    public String getArgument()
    {
        return argument;
    }
}
