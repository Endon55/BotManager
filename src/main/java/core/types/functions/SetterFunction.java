package core.types.functions;

public class SetterFunction<T, U>
{
    java.util.function.BiConsumer<T, U> setter;
    
    public SetterFunction(java.util.function.BiConsumer<T, U> setterFunction)
    {
        this.setter = setterFunction;
    }
    
    public void set(T object, U setValue)
    {
        if(object == null) return;
        if(getSetter() == null) return;
        
        getSetter().accept(object, setValue);
    }

    
    public java.util.function.BiConsumer<T, U> getSetter()
    {
        return setter;
    }
}
