package core.gui.tabs;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import core.Core;
import core.clients.ClientManager;
import core.clients.ConnectionStatus;
import core.clients.sockets.Session;
import core.database.tables.Proxy;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.WorkerAccount;
import core.gui.impl.ComboBox;
import core.gui.impl.IntField;
import core.gui.impl.StatusListRenderer;
import core.types.Shift;
import core.types.functions.ComplexFunction;
import lombok.extern.log4j.Log4j2;
import types.accounts.Personality;
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
import java.util.Optional;

@Log4j2
public class WorkerTab extends ScrollWithFields<WorkerAccount>
{
    public static DefaultComboBoxModel<WorkerAccount> ACCOUNTS_MODEL = new DefaultComboBoxModel<>();
    Icon dreambotIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/DreambotIconSmol.png")));
    Icon runeliteIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/RuneliteIconSmol.png")));
    Icon muleIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/Mule.png")));
    Icon wrenchIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/Wrench32.png")));
    
    JPanel detailsPanel;
    public WorkerTab(Component parentComponent, Core core)
    {
        super(core, parentComponent, "Worker", "Workers");
    }
    @Override
    public JPanel getDetailsPanel()
    {
        return detailsPanel;
    }

    @Override
    public List<WorkerAccount> getData()
    {
        return getCore().getDatabase().getAccountProvisioner().getWorkers();
    }
    
    @Override
    public void init()
    {
        for(WorkerAccount account : getData())
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
    
        JLabel personalityLabel = new JLabel("Personality:");
        ComboBox<Personality> personalityComboBox = new ComboBox<>(Personality.values(), false);
        
        JLabel shiftLabel = new JLabel("Shift:");
        ComboBox<Shift> shiftComboBox = new ComboBox<>(Shift.values(), false);
        
        JLabel taskLabel = new JLabel("Task:");
        ComboBox<Task> taskComboBox = new ComboBox<>(Task.values(), false);
        
        JLabel proxyLabel = new JLabel("Proxy:");
        ComboBox<Proxy> proxyComboBox = new ComboBox<>(ProxiesTab.PROXIES_MODEL, false);
    
        JLabel notesLabel = new JLabel("Notes:");
        JTextField notesField = new JTextField();
        
        JLabel muleLabel = new JLabel("Mule:");
        JTextField muleField = new JTextField();
    
    
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
                                        .addComponent(taskLabel)
                                        .addComponent(shiftLabel)
                                        .addComponent(personalityLabel)
                                    
                                        .addComponent(birthdayLabel)
                                        .addComponent(creationLabel)
                                        .addComponent(proxyLabel)
                                        .addComponent(muleLabel)
                                        .addComponent(notesLabel))
                                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(displayNameField)
                                        .addComponent(emailField)
                                        .addComponent(passwordField)
                                        .addComponent(pinField)
        
                                        .addComponent(taskComboBox)
                                        
                                        .addComponent(gameTimeField)
                                        .addComponent(cashField)
                                        .addComponent(taskComboBox)
                                        .addComponent(shiftComboBox)
                                        .addComponent(personalityComboBox)
                                    
                                        .addComponent(birthdayDatePicker)
                                        .addComponent(creationDatePicker)
                                        .addComponent(proxyComboBox)
                                        .addComponent(muleField)
                                        .addComponent(notesField)
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
                        .addComponent(shiftLabel)
                        .addComponent(shiftComboBox))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(personalityLabel)
                        .addComponent(personalityComboBox))
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
                        .addComponent(proxyComboBox))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(muleLabel)
                        .addComponent(muleField))
                .addGroup(accountInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(notesLabel)
                        .addComponent(notesField))
                .addComponent(actionsPanel)
        );
        setListRender(new StatusListRenderer(getCore().getClientManager()));
    
        registerField(displayNameLabel, displayNameField, false, new ComplexFunction<>(Account::getDisplayName, Account::setDisplayName));
        registerField(emailLabel, emailField, false, new ComplexFunction<>(Account::getEmail, Account::setEmail));
        registerField(passwordLabel, passwordField, false, new ComplexFunction<>(Account::getPassword, Account::setPassword));
        registerField(pinLabel, pinField, false, new ComplexFunction<>(Account::getBankPin, Account::setBankPin));
        registerField(taskLabel, taskComboBox, false, new ComplexFunction<>(WorkerAccount::getTask, WorkerAccount::setTask));
        registerField(personalityLabel, personalityComboBox, false, new ComplexFunction<>(WorkerAccount::getPersonality, WorkerAccount::setPersonality));
        registerField(shiftLabel, shiftComboBox, false, new ComplexFunction<>(WorkerAccount::getShift, WorkerAccount::setShift));
        registerField(gameTimeLabel, gameTimeField, false, new ComplexFunction<>(Account::getGameTime, Account::setGameTime));
        registerField(cashLabel, cashField, false, new ComplexFunction<>(Account::getCash, Account::setCash));
        registerField(proxyLabel, proxyComboBox, false, new ComplexFunction<>(Account::getProxy, Account::setProxy));
        registerField(creationLabel, creationDatePicker, false, new ComplexFunction<>(Account::getCreationDate, Account::setCreationDate));
        registerField(birthdayLabel, birthdayDatePicker, false, new ComplexFunction<>(Account::getBirthday, Account::setBirthday));
        registerField(muleLabel, muleField, false, new ComplexFunction<>(WorkerAccount::getMule, WorkerAccount::setMule));
        registerField(notesLabel, notesField, false, new ComplexFunction<>(Account::getNotes, Account::setNotes));
    }
    
    @Override
    public JPanel getActionsPanel()
    {
        JPanel actionsPanel = new JPanel();
        JLabel launchLabel = new JLabel("Launch with ");
        JLabel orLabel = new JLabel(" or ");
    
        JButton dreambotButton = new JButton(dreambotIcon);
        dreambotButton.setPreferredSize(new Dimension(32, 32));
        dreambotButton.setMaximumSize(new Dimension(32, 32));
        dreambotButton.setBorderPainted(false);
        dreambotButton.setBackground(clear);
        dreambotButton.setContentAreaFilled(false);
        dreambotButton.addActionListener(e ->
        {
            Account account = getSelectedValue();
            ConnectionStatus status = getCore().getClientManager().getConnectionStatus(account.getId());
            if(status == ConnectionStatus.CONNECTING)
            {
                log.info("Account is already logged in.");
            }
            else if(status == ConnectionStatus.CONNECTED)
            {
                log.info("Client is already open.");
            }
            else
            {
                getCore().getClientManager().launch(account);
            }
        });
    
    
        JButton runeliteButton = new JButton(runeliteIcon);
        runeliteButton.setPreferredSize(new Dimension(32, 32));
        runeliteButton.setMaximumSize(new Dimension(32, 32));
        runeliteButton.setMaximumSize(new Dimension(32, 32));
        runeliteButton.setBorderPainted(false);
        runeliteButton.setContentAreaFilled(false);
        runeliteButton.setBackground(clear);
    
        JButton muleButton = new JButton(muleIcon);
        muleButton.setPreferredSize(new Dimension(32, 32));
        muleButton.setMaximumSize(new Dimension(32, 32));
        muleButton.setBorderPainted(false);
        muleButton.setContentAreaFilled(false);
        muleButton.setBackground(clear);
    
        muleButton.addActionListener(e ->
        {
            Optional<Session> session = ClientManager.getSession(getSelectedValue().getId());
            if(session.isPresent())
            {
                session.get().callForMule();
            }
            else
            {
                log.info("Account is not connected, try launching a new client ya-dingus.");
            }
        });
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
    
        JLabel muleLabel = new JLabel("Call Mule");
        JLabel testLabel = new JLabel("Test");
        actionsLayout.setHorizontalGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
            
                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(actionsLayout.createSequentialGroup()
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(launchLabel))
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(dreambotButton))
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(orLabel))
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(runeliteButton))
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(muleLabel))
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(muleButton))
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(orLabel)
                                        .addComponent(testLabel))
                                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(wrenchButton))
                        )
                )
        );
    
    
        actionsLayout.setVerticalGroup(actionsLayout.createSequentialGroup()
                .addGroup(actionsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(launchLabel)
                        .addComponent(dreambotButton)
                        .addComponent(orLabel)
                        .addComponent(runeliteButton)
                        .addComponent(muleLabel)
                        .addComponent(muleButton)
                        .addComponent(testLabel)
                        .addComponent(wrenchButton)));
    
    
        actionsLayout.linkSize(dreambotButton, runeliteButton, muleButton, wrenchButton);
        return  actionsPanel;
    }
    
    @Override
    public WorkerAccount getBlankWithDefaultValues()
    {
        WorkerAccount account = new WorkerAccount();
        
        return account;
    }
    
    @Override
    public void updateSelection()
    {
    }
    
    @Override
    public void updateTable(WorkerAccount saveObject)
    {
        ACCOUNTS_MODEL.addElement(saveObject);
    }
    
    @Override
    public void saveToDatabase(WorkerAccount saveObject)
    {
        //getCore().getDatabase().getAccountProvisioner().saveOr(saveObject);
    }
    
    
}
