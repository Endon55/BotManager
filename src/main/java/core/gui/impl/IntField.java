package core.gui.impl;

import javax.swing.JFormattedTextField;

public class IntField extends JFormattedTextField
{
    int defaultValue;
    public IntField(int defaultValue, int maxCharacters)
    {
        this.defaultValue = defaultValue;
        this.setDocument(new NumbersOnlyDocument(maxCharacters));
        
    }
    
    public int getNumber()
    {
        String text = getText();
        if(text == null || text.equals("")) return defaultValue;
        
        return Integer.parseInt(getText());
    }
    public void setNumber(int number)
    {
        setText("" + number);
    }
    
    public int getDefaultValue()
    {
        return defaultValue;
    }
}
