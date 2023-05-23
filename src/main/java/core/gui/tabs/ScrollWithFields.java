package core.gui.tabs;

import com.github.lgooddatepicker.components.DatePicker;
import core.Core;
import core.gui.GUI;
import core.gui.enums.Dialogue;
import core.gui.impl.ComboBox;
import core.gui.impl.FieldComponent;
import core.gui.impl.IntField;
import core.gui.impl.LongField;
import core.gui.impl.MappedField;
import core.types.functions.ComplexFunction;
import core.types.functions.GetterFunction;
import core.types.functions.SetterFunction;
import core.utilities.Utils;
import lombok.extern.log4j.Log4j2;
import tools.utils.NullSafe;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@Log4j2
public abstract class ScrollWithFields<T> implements ITab
{
    private final Icon editIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/Pencil24.png")));
    private final Icon saveIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/FloppyDisk24.png")));
    
    public final Color clear = new Color(0, 0, 0, 0);
    
    private boolean editMode = false;
    private boolean newEntry = false;
    
    private final List<MappedField> mappedFields = new ArrayList<>();
    
    private final Component parentComponent;
    private final JPanel detailsTitlePanel = new JPanel();
    private JButton modifyButton;
    private JPanel mainPanel = new JPanel();
    
    JList<T> list = new JList<>();
    DefaultListModel<T> listModel = new DefaultListModel<>();
    DefaultListCellRenderer renderer;
    
    private final String name;
    private final String pluralName;
    private final Core core;
    
    public ScrollWithFields(Core core, Component parentComponent, String name, String pluralName)
    {
        this.core = core;
        this.name = name;
        this.pluralName = pluralName;
        this.parentComponent = parentComponent;
        init();
    }
   
    public JPanel getPanel()
    {
        
        list.setFixedCellHeight(20);
        JPanel listPanel = new JPanel();
        JLabel listTitleLabel = new JLabel(pluralName);
        JPanel detailsPanel = new JPanel();
        JLabel detailsTitleLabel = new JLabel(name + " Details");
    
        detailsTitlePanel.setLayout(new BoxLayout(detailsTitlePanel, BoxLayout.X_AXIS));
        detailsTitlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsTitlePanel.add(detailsTitleLabel);
        modifyButton = getModifyButton();
        detailsTitlePanel.add(modifyButton);
    
        //detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        //detailsPanel.add(detailsTitlePanel);
        JPanel childDetailsPanel = getDetailsPanel();
        JPanel childActionsPanel = getActionsPanel();
        
        GroupLayout detailsLayout = new GroupLayout(detailsPanel);
        JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(10, 10000));
        if(childActionsPanel != null)
        {
            JPanel actionsTitlePanel = new JPanel();
            JLabel actionsLabel = new JLabel("Actions");
            
            actionsTitlePanel.setLayout(new BoxLayout(actionsTitlePanel, BoxLayout.X_AXIS));
            actionsTitlePanel.add(actionsLabel);
            detailsLayout.setHorizontalGroup(detailsLayout.createSequentialGroup()
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(detailsTitlePanel)
                            .addComponent(childDetailsPanel)
                            .addComponent(actionsTitlePanel)
                            .addComponent(childActionsPanel)
                            .addComponent(emptyPanel)
                    ));
            detailsLayout.setVerticalGroup(detailsLayout.createSequentialGroup()
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(detailsTitlePanel))
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(childDetailsPanel))
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(actionsTitlePanel))
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(childActionsPanel))
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(emptyPanel))
            );
        }else{
            detailsLayout.setHorizontalGroup(detailsLayout.createSequentialGroup()
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(detailsTitlePanel)
                            .addComponent(childDetailsPanel)
                            .addComponent(emptyPanel)
                    ));
            detailsLayout.setVerticalGroup(detailsLayout.createSequentialGroup()
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(detailsTitlePanel))
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(childDetailsPanel))
                    .addGroup(detailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(emptyPanel)));
        }
    
        detailsPanel.setLayout(detailsLayout);
    
        
        for(T t : getData())
        {
            listModel.addElement(t);
        }
        list.setModel(listModel);
        list.addListSelectionListener(new ListSelectionListener()
        {
            
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                T t = list.getSelectedValue();
                
                T tFirst = listModel.getElementAt(e.getFirstIndex());
                T tLast = listModel.getElementAt(e.getLastIndex());
                T tt = t == tFirst ? tLast : tFirst;
                if(editMode)
                {
                    if(!saveChanges(t == tFirst ? tLast : tFirst))
                    {
                        return;
                    }
                }
                for(MappedField field : mappedFields)
                {
                    FieldComponent component = field.getField();
                    GetterFunction getter = field.getFunction().getGetterFunction();
                    
                    if(component.getComponent() instanceof LongField)
                    {
                        ((LongField) component.getComponent()).setNumber( NullSafe.of((long) getter.get(t))
                                .getOrDefault(1L));
                    }
                    if(component.getComponent() instanceof IntField)
                    {
                        ((IntField) component.getComponent()).setNumber((int) NullSafe.of( getter.get(t)).getOrDefault(1));
                    }
                    else if(component.getComponent() instanceof JTextField)
                    {
                        ((JTextField) component.getComponent()).setText(NullSafe.of(getter.get(t)).call(Object::toString).getOrDefault(""));
                    }
                    else if(component.getComponent() instanceof ComboBox)
                    {
                        ((ComboBox) component.getComponent()).setSelectedItem(NullSafe.of(getter.get(t)).getOrDefault(""));
                    }
                    else if(component.getComponent() instanceof DatePicker)
                    {
                        ((DatePicker) component.getComponent()).setDate(Utils.zonedToLocal((ZonedDateTime) getter.get(t)));
                    }
                }
                updateSelection();
            }
        });
        
        
 
        
        JButton newEntryButton = new JButton("Add " + name);
        newEntryButton.setMargin(new Insets(0, 0, 0, 0));
        
        
        newEntryButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(!newEntry)
                {
                    newEntry = true;
                    T blank = getBlankWithDefaultValues();
                    listModel.addElement(blank);
                    list.setSelectedValue(blank, true);
                    toggleModify(true);
                }
            }
        });
        
        
        
        JScrollPane listScrollPane = new JScrollPane(list);
        listPanel.setLayout(new GridBagLayout());
        listPanel.setMaximumSize(new Dimension(10, 1000));
        
        
        GridBagConstraints cTitle = new GridBagConstraints();
        cTitle.fill = GridBagConstraints.CENTER;
        cTitle.weighty = 0;
        cTitle.gridy = 0;
        cTitle.gridx = 0;
        cTitle.ipady = 10;
        listPanel.add(listTitleLabel, cTitle);
        
        GridBagConstraints cList = new GridBagConstraints();
        cList.fill = GridBagConstraints.BOTH;
        cList.weighty = 1;
        cList.gridx = 0;
        cList.gridy = GridBagConstraints.RELATIVE;
        cList.gridheight = 5;
        listPanel.add(listScrollPane, cList);
        
        GridBagConstraints cNew = new GridBagConstraints();
        cNew.fill = GridBagConstraints.HORIZONTAL;
        cNew.weighty = 0;
        cNew.gridx = 0;
        cNew.gridy = GridBagConstraints.RELATIVE;
        listPanel.add(newEntryButton, cNew);
        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(listPanel);
        mainPanel.add(detailsPanel);
        

        setEnabled(false);
        list.setSelectedIndex(0);
        return mainPanel;
    }
    
    public boolean saveChanges()
    {
        return saveChanges(getSelectedValue());
    }
    
    public boolean saveChanges(T t)
    {
        if(isModified())
        {
            int result = JOptionPane.showConfirmDialog(parentComponent, "Save changes to " + t.toString() + "?", "Save Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if(result == 0) //Yes
            {
                updateTable(t);
                
                if(!saveFunctionality(t))
                {
                    return false;
                }
                newEntry = false;
                GUI.invalidate();
            }
            else if(result == 1) //No
            {
                discardChanges();
            }
            //if cancel we do nothing, hence the blank spot
        }
        
        toggleModify(false);
        return true;
    }
    
    public Dialogue saveConfirmPanel(T t)
    {
        int result =  JOptionPane.showConfirmDialog(parentComponent, "Save changes to " + t.toString() + "?", "Save Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if(result == 1) return Dialogue.YES;
        if(result == 2) return Dialogue.NO;
        return Dialogue.CANCEL;
        
    }
    
    private boolean saveFunctionality(T t)
    {
        for(int i = 0; i < mappedFields.size(); i++)
        {
            MappedField entry = mappedFields.get(i);
            
            FieldComponent fieldComponent = entry.getField();
            Component component = fieldComponent.getComponent();
            SetterFunction setter = entry.getFunction().getSetterFunction();
            GetterFunction getter = entry.getFunction().getGetterFunction();
            
            Object getterValue = getter.get(t);
            Object fieldValue = null;
            
            if(component instanceof IntField)
            {
                fieldValue = ((IntField) component).getNumber();
            }
            else if(component instanceof LongField)
            {
                fieldValue = ((LongField) component).getNumber();
            }
            else if(component instanceof ComboBox)
            {
                fieldValue = ((ComboBox) component).getSelectedItem();
            }
            else if(component instanceof DatePicker)
            {
                fieldValue = Utils.localToZoned(((DatePicker) component).getDate());
            }
            else if(component instanceof JTextField)
            {
                String text = ((JTextField) component).getText();
                fieldValue = text;
                if(!fieldComponent.isAllowedEmpty() && text.isEmpty())
                {
                    JOptionPane.showConfirmDialog(parentComponent, fieldComponent.getLabel()
                                    .getText() + " is not allowed to be empty.", "Empty Field",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    return false;
                }
            }
            if(i == 0)
            {
                
                if(!isNewEntry(entry, fieldValue))
                {
                    JOptionPane.showConfirmDialog(parentComponent, fieldComponent.getLabel()
                                    .getText() + " must be Unique.", "Conflicting Entries",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    return false;
                }
            }
            if(!fieldComponent.isAllowedEmpty() && fieldValue == null)
            {
                JOptionPane.showConfirmDialog(parentComponent, fieldComponent.getLabel().getText() + " is not allowed to be empty.", "Empty Field",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                return false;
            }
    
            setter.set(t, fieldValue);
        }
        
        saveToDatabase(t);
        
        return true;
        
    }
    
    private void discardChanges()
    {
        if(newEntry)
        {
            listModel.removeElementAt(list.getSelectedIndex());
            list.invalidate();
            list.setSelectedIndex(0);
            
            newEntry = false;
        }
    }
    
    public void setEnabled(boolean enabled)
    {
        for(MappedField field : mappedFields)
        {
            Component component = field.getField().getComponent();
            
            if(component instanceof IntField)
            {
                ((IntField) component).setEditable(enabled);
            }
            if(component instanceof JTextField)
            {
                ((JTextField) component).setEditable(enabled);
            }
            else
            {
                component.setEnabled(enabled);
            }
        }
    }
    
    private boolean isNewEntry(MappedField field, Object key)
    {
        for(T t: Collections.list(listModel.elements()))
        {
            if(field.getFunction().getGetterFunction() == key)
            {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isModified()
    {
        T t = getSelectedValue();
        for(MappedField entry :
                mappedFields)
        {
            if(isFieldModified(entry))
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean isFieldModified(MappedField fieldEntry)
    {
        Component component = fieldEntry.getField().getComponent();
        GetterFunction getter = fieldEntry.getFunction().getGetterFunction();
        
        T selectedValue = getSelectedValue();
        
        Object objectValue = getter.get(selectedValue);
        Object fieldValue = null;
        
        if(component instanceof IntField)
        {
            fieldValue = ((IntField) component).getNumber();
        }
        else if(component instanceof ComboBox)
        {
            fieldValue = ((ComboBox) component).getSelectedItem();
        }
        else if(component instanceof DatePicker)
        {
            fieldValue = ((DatePicker) component).getDate();
        }
        else if(component instanceof JTextField)
        {
            fieldValue = ((JTextField) component).getText();
        }
        if(objectValue == null && fieldValue != null)
        {
            return true;
        }
        if(objectValue != null && !objectValue.equals(fieldValue))
        {
            return true;
        }
        return false;
    }
    

    
    private void toggleModify(boolean modifying)
    {
        editMode = modifying;
        setEnabled(editMode);
        detailsTitlePanel.remove(modifyButton);
        modifyButton = getModifyButton();
        detailsTitlePanel.add(modifyButton);
        mainPanel.revalidate();
    }
    
    private JButton getModifyButton()
    {
        if(editMode)
        {
            JButton saveButton = createFormattedButton(24, 24, "Edit?", saveIcon);
            
            saveButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    saveChanges();
                }
            });
            
            return saveButton;
        }
        else
        {
            JButton editButton = createFormattedButton(24, 24, "Edit?", editIcon);
            
            editButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    toggleModify(true);
                }
            });
            
            return editButton;
        }
    }
    
    public JButton createFormattedButton(int width, int height, String tooltip, Icon icon)
    {
        JButton button = new JButton(icon);
        button.setMaximumSize(new Dimension(width, height));
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(clear);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setToolTipText(tooltip);
        return button;
    }
    
    void setListRender(DefaultListCellRenderer renderer)
    {
        this.renderer = renderer;
        list.setCellRenderer(renderer);
    }
    public void update()
    {
        if(renderer != null)
        {
            renderer.invalidate();
            renderer.repaint();
        }
    }
    public T getSelectedValue()
    {
        return list.getSelectedValue();
    }
    
    public void registerField(FieldComponent fieldComponent, ComplexFunction complexFunction)
    {
        mappedFields.add(new MappedField(fieldComponent, complexFunction));
    }
    
    public void registerField(JLabel label, Component component, boolean allowEmptyOrNull, ComplexFunction complexFunction)
    {
        mappedFields.add(new MappedField(new FieldComponent(label, component, allowEmptyOrNull), complexFunction));
    }
    
    public Core getCore()
    {
        return core;
    }
    public abstract void init();
    public abstract List<T> getData();
    
    public abstract JPanel getDetailsPanel();
    
    public abstract JPanel getActionsPanel();
    
    public abstract T getBlankWithDefaultValues();
    
    public abstract void updateSelection();
    
    public abstract void updateTable(T saveObject);
    
    public abstract void saveToDatabase(T saveObject);
}
