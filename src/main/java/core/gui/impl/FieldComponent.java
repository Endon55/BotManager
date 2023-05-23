package core.gui.impl;

import javax.swing.*;
import java.awt.*;

public class FieldComponent
{
    JLabel label;
    Component component;
    boolean allowEmpty;
    
    public FieldComponent(JLabel label, Component component, boolean allowEmptyOrNull)
    {
        this.label = label;
        this.component = component;
        this.allowEmpty = allowEmptyOrNull;
    }
    public FieldComponent(String componentName, Component component, boolean allowEmptyOrNull)
    {
        this(new JLabel(componentName), component, allowEmptyOrNull);
    }
    

    public JLabel getLabel()
    {
        return label;
    }
    
    public void setLabel(JLabel label)
    {
        this.label = label;
    }
    
    public Component getComponent()
    {
        return component;
    }
    
    public void setComponent(Component component)
    {
        this.component = component;
    }
    public void setEnabled(boolean enable)
    {
        this.component.enableInputMethods(enable);
    }
    
    public boolean isAllowedEmpty()
    {
        return allowEmpty;
    }
}
