package core.gui.impl;

import core.types.functions.*;

import javax.swing.*;
import java.awt.*;

public class MappedField
{
    FieldComponent field;
    ComplexFunction function;
    
    public MappedField(FieldComponent field, ComplexFunction function)
    {
        this.field = field;
        this.function = function;
    }
    
    public FieldComponent getField()
    {
        return field;
    }
    
    public ComplexFunction getFunction()
    {
        return function;
    }
}
