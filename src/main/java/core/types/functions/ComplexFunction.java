package core.types.functions;

public class ComplexFunction<T, U>
{
    private GetterFunction<T, U> getterFunction;
    private SetterFunction<T, U> setterFunction;

    public ComplexFunction(java.util.function.Function<T, U> getterFunction, java.util.function.BiConsumer<T, U> setterFunction)
    {
        this.getterFunction = new GetterFunction<>(getterFunction);
        this.setterFunction = new SetterFunction<>(setterFunction);
    }
    
    
    public GetterFunction<T, U> getGetterFunction()
    {
        return getterFunction;
    }
    
    public SetterFunction<T, U> getSetterFunction()
    {
        return setterFunction;
    }
}
