package core.gui.impl;

import lombok.extern.log4j.*;

import javax.swing.text.*;
@Log4j2
public class NumbersOnlyDocument extends PlainDocument
{
    private final int maxCharacters;
    public NumbersOnlyDocument(int maxCharacters)
    {
        super();
        this.maxCharacters = maxCharacters;
    }
    
    @Override
    public void insertString(int offset, String txt, AttributeSet a)
    {
        try
        {
            String text = getText(0, getLength());
            if((text + txt).matches("-[0-9]{0," + maxCharacters + "}"))
            {
                super.insertString(offset, txt, a);
            }
        } catch(Exception e)
        {
            log.warn("Failed to create document", e);
        }
        
    }
}

