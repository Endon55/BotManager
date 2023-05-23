package core.gui.impl;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ComboBox<T> extends JComboBox<T>
{
    
    MouseListener[] mouseListeners;
    MouseListener[] buttonMouseListeners;
    JTextField textField;
    AbstractButton button;
    boolean editable;
    Color backgroundColor;
    
    public ComboBox(T[] t, boolean editable)
    {
        super(t);
        this.editable = editable;
        this.setEditable(editable);
        this.backgroundColor = getBackground();
        setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
        
    }
    
    public ComboBox(DefaultComboBoxModel<T> model, boolean editable)
    {
        super(model);
        this.editable = editable;
        this.setEditable(editable);
        this.backgroundColor = getBackground();
        setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
        
    }
    
    @Override
    public void setEnabled(boolean enabled)
    {
       if(!enabled)
       {
           enableReadOnly();
       }else{
           disableReadOnly();
       }
    }
    
    
    private void enableReadOnly()
    {
        textField = (JTextField) getEditor().getEditorComponent();
        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.setEditable(false);
        setBackground(backgroundColor);
        mouseListeners = getMouseListeners();
        for(MouseListener listener : mouseListeners)
            removeMouseListener(listener);
        
        for(Component component : getComponents())
        {
            if(component instanceof AbstractButton)
            {
                button = (AbstractButton) component;
                button.setEnabled(false);
    
                buttonMouseListeners = button.getMouseListeners();
                for(MouseListener listener : buttonMouseListeners)
                    button.removeMouseListener(listener);
            }
        }
    }
    
    private void disableReadOnly()
    {
        textField.setEditable(editable);
        setBackground(Color.WHITE);
        
        for(MouseListener listener : mouseListeners)
            addMouseListener(listener);
        button.setEnabled(true);
        for(MouseListener listener : buttonMouseListeners)
            button.addMouseListener(listener);
    }
}
