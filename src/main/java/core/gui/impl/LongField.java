package core.gui.impl;

import javax.swing.JFormattedTextField;

public class LongField extends JFormattedTextField
{
    long defaultValue;
    public LongField(long defaultValue, int maxCharacters)
    {
        this.defaultValue = defaultValue;
        this.setDocument(new NumbersOnlyDocument(maxCharacters));
        
    }
    
    public long getNumber()
    {
        String text = getText();
        if(text == null || text.equals("")) return defaultValue;
        
        return Long.parseLong(getText());
    }
    public void setNumber(long number)
    {
        setText("" + number);
    }
    
    public long getDefaultValue()
    {
        return defaultValue;
    }
}
