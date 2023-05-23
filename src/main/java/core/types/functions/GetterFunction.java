package core.types.functions;

public class GetterFunction<T, U>
{
    java.util.function.Function<T, U> function;
    public GetterFunction(java.util.function.Function<T, U> function)
    {
        this.function = function;
        
    }
    public U get(T t)
    {
        if(t == null) return null;
        return function.apply(t);
    }
    
    public java.util.function.Function<T, U> getFunction()
    {
        return function;
    }
}
