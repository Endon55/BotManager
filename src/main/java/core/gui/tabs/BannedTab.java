package core.gui.tabs;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import core.Core;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.BannedAccount;
import core.gui.impl.ComboBox;
import core.gui.impl.IntField;
import core.gui.impl.LongField;
import core.gui.impl.StatusListRenderer;
import core.types.functions.ComplexFunction;
import lombok.extern.log4j.Log4j2;
import types.accounts.Task;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.Objects;

@Log4j2
public class BannedTab extends ScrollWithFields<BannedAccount>
{
    public static DefaultComboBoxModel<BannedAccount> ACCOUNTS_MODEL = new DefaultComboBoxModel<>();
    Icon dreambotIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/DreambotIconSmol.png")));
    Icon runeliteIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/RuneliteIconSmol.png")));
    Icon muleIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/Mule.png")));
    Icon wrenchIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/Wrench32.png")));
    
    JPanel detailsPanel;
    public BannedTab(Component parentComponent, Core core)
    {
        super(core, parentComponent, "Worker", "Workers");
    }
    @Override
    public JPanel getDetailsPanel()
    {
        return detailsPanel;
    }

    @Override
    public List<BannedAccount> getData()
    {
        return getCore().getDatabase().getAccountProvisioner().getBanned();
    }
    
    @Override
    public void init()
    {
        for(BannedAccount account : getData())
        {
            ACCOUNTS_MODEL.addElement(account);
        }
        
        
        detailsPanel = new JPanel();
        
        JLabel displayNameLabel = new JLabel("Display Name:");
        JTextField displayNameField = new JTextField();
    
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
    
        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordField = new JTextField();
    
        JLabel pinLabel = new JLabel("Bank Pin:");
        IntField pinField = new IntField(-1, 4);
    
        JLabel gameTimeLabel = new JLabel("Game Time:");
        IntField gameTimeField = new IntField(0, 2);
    
        JLabel cashLabel = new JLabel("Cash: ");
        IntField cashField = new IntField(0, 12);
    
    
        JLabel birthdayLabel = new JLabel("Birthday:");
        DatePickerSettings birthdaySettings = new DatePickerSettings();
        birthdaySettings.setEnableMonthMenu(true);
        birthdaySettings.setEnableYearMenu(true);
        DatePicker birthdayDatePicker = new DatePicker(birthdaySettings);
    
        JLabel creationLabel = new JLabel("Creation:");
        DatePickerSettings creationSettings = new DatePickerSettings();
        creationSettings.setEnableMonthMenu(true);
        creationSettings.setEnableYearMenu(true);
        DatePicker creationDatePicker = new DatePicker(creationSettings);

        JLabel taskLabel = new JLabel("Task:");
        ComboBox<Task> taskComboBox = new ComboBox<>(Task.values(), false);
        
        JLabel proxyLabel = new JLabel("Proxy:");
        JTextField proxyField = new JTextField();//new ComboBox<>(ProxiesTab.PROXIES_MODEL, false);
    
        JLabel notesLabel = new JLabel("Notes:");
        JTextField notesField = new JTextField();
        
        JLabel banDateLabel = new JLabel("Ban Date:");
        DatePickerSettings banDateSettings = new DatePickerSettings();
        banDateSettings.setEnableMonthMenu(true);
        banDateSettings.setEnableYearMenu(true);
        DatePicker banDatePicker = new DatePicker(banDateSettings);
        
        JLabel runtimeLabel = new JLabel("Run Time:");
        LongField runtimeField = new LongField(0, 15);
    
    
        GroupLayout accountInfoLayout = new GroupLayout(detailsPanel);
        detailsPanel.setLayout(accountInfoLayout);
        accountInfoLayout.setAutoCreateGaps(true);
        accountInfoLayout.setAutoCreateContainerGaps(true);
    
        JPanel actionsPanel = new JPanel();
        accountInfoLayout.setHorizontalGroup(accountInfoLayout.createSequentialGroup()
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(accountInfoLayout.createSequentialGroup()
                                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(displayNameLabel)
                                        .addComponent(emailLabel)
                                        .addComponent(passwordLabel)
                                        .addComponent(pinLabel)
                                        
                                        .addComponent(taskLabel)
                                        
                                        .addComponent(gameTimeLabel)
                                        .addComponent(cashLabel)
                                    
                                        .addComponent(birthdayLabel)
                                        .addComponent(creationLabel)
                                        .addComponent(proxyLabel)
                                        .addComponent(notesLabel)
                                        .addComponent(banDateLabel)
                                        .addComponent(runtimeLabel))
                                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(displayNameField)
                                        .addComponent(emailField)
                                        .addComponent(passwordField)
                                        .addComponent(pinField)
                                        
                                        .addComponent(taskComboBox)
                                    
                                        .addComponent(gameTimeField)
                                        .addComponent(cashField)
                                    
                                        .addComponent(birthdayDatePicker)
                                        .addComponent(creationDatePicker)
                                        .addComponent(proxyField)
                                        .addComponent(notesField)
                                        .addComponent(banDatePicker)
                                        .addComponent(runtimeField)
                                ))
                        .addComponent(actionsPanel))
        );
    
        accountInfoLayout.setVerticalGroup(accountInfoLayout.createSequentialGroup()
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(displayNameLabel)
                        .addComponent(displayNameField))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(emailLabel)
                        .addComponent(emailField))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(pinLabel)
                        .addComponent(pinField))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(taskLabel)
                        .addComponent(taskComboBox))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(gameTimeLabel)
                        .addComponent(gameTimeField))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(cashLabel)
                        .addComponent(cashField))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(birthdayLabel)
                        .addComponent(birthdayDatePicker))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(creationLabel)
                        .addComponent(creationDatePicker))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(proxyLabel)
                        .addComponent(proxyField))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(notesLabel)
                        .addComponent(notesField))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(banDateLabel)
                        .addComponent(banDatePicker))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(runtimeLabel)
                        .addComponent(runtimeField))
                .addComponent(actionsPanel)
        );
        setListRender(new StatusListRenderer(getCore().getClientManager()));
    
        registerField(displayNameLabel, displayNameField, false, new ComplexFunction<>(Account::getDisplayName, Account::setDisplayName));
        registerField(emailLabel, emailField, false, new ComplexFunction<>(Account::getEmail, Account::setEmail));
        registerField(passwordLabel, passwordField, false, new ComplexFunction<>(Account::getPassword, Account::setPassword));
        registerField(pinLabel, pinField, false, new ComplexFunction<>(Account::getBankPin, Account::setBankPin));
        registerField(taskLabel, taskComboBox, false, new ComplexFunction<>(BannedAccount::getTask, BannedAccount::setTask));
        registerField(gameTimeLabel, gameTimeField, false, new ComplexFunction<>(Account::getGameTime, Account::setGameTime));
        registerField(cashLabel, cashField, false, new ComplexFunction<>(Account::getCash, Account::setCash));
        registerField(proxyLabel, proxyField, false, new ComplexFunction<>(Account::getProxy, Account::setProxy));
        registerField(creationLabel, creationDatePicker, false, new ComplexFunction<>(Account::getCreationDate, Account::setCreationDate));
        registerField(birthdayLabel, birthdayDatePicker, false, new ComplexFunction<>(Account::getBirthday, Account::setBirthday));
        registerField(notesLabel, notesField, false, new ComplexFunction<>(Account::getNotes, Account::setNotes));
        registerField(banDateLabel, banDatePicker, false, new ComplexFunction<>(BannedAccount::getBanDate, BannedAccount::setBanDate));
        registerField(runtimeLabel, runtimeField, false, new ComplexFunction<>(BannedAccount::getRuntime, BannedAccount::setRuntime));
    }
    
    @Override
    public JPanel getActionsPanel()
    {
        JPanel actionsPanel = new JPanel();


        JButton wrenchButton = new JButton(wrenchIcon);
        wrenchButton.setPreferredSize(new Dimension(32, 32));
        wrenchButton.setMaximumSize(new Dimension(32, 32));
        wrenchButton.setBorderPainted(false);
        wrenchButton.setContentAreaFilled(false);
        wrenchButton.setBackground(clear);
    
        GroupLayout actionsLayout = new GroupLayout(actionsPanel);
        actionsPanel.setLayout(actionsLayout);
        actionsLayout.setAutoCreateGaps(true);
        actionsLayout.setAutoCreateContainerGaps(true);
    
        JLabel testLabel = new JLabel("Test");
        actionsLayout.setHorizontalGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
            
                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(actionsLayout.createSequentialGroup()
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(testLabel))
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(wrenchButton))
                        )
                )
        );
    
    
        actionsLayout.setVerticalGroup(actionsLayout.createSequentialGroup()
                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(testLabel)
                        .addComponent(wrenchButton)));
    
    
        actionsLayout.linkSize(wrenchButton);
        return  actionsPanel;
    }
    
    @Override
    public BannedAccount getBlankWithDefaultValues()
    {
        BannedAccount account = new BannedAccount();
        

        return account;
    }
    
    @Override
    public void updateSelection()
    {
    
    }
    
    @Override
    public void updateTable(BannedAccount saveObject)
    {
        ACCOUNTS_MODEL.addElement(saveObject);
    }
    
    @Override
    public void saveToDatabase(BannedAccount saveObject)
    {
        //getCore().getDatabase().getAccountProvisioner().saveOr(saveObject);
    }
    
    
}
