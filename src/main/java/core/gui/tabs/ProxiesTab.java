package core.gui.tabs;

import core.Core;
import core.database.tables.Proxy;
import core.gui.impl.IntField;
import core.types.functions.ComplexFunction;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.util.List;

public class ProxiesTab extends ScrollWithFields<Proxy>
{
    public static DefaultComboBoxModel<Proxy> PROXIES_MODEL = new DefaultComboBoxModel<>();
    JPanel detailsPanel;
    
    public ProxiesTab(Component parentComponent, Core core)
    {
        super(core, parentComponent, "Proxy", "Proxies");
    }
    
    @Override
    public void init()
    {
        for(Proxy proxy : getData())
        {
            PROXIES_MODEL.addElement(proxy);
        }
        detailsPanel = new JPanel();
        
        JLabel providerLabel = new JLabel("Provider:");
        JTextField providerField = new JTextField();
    
        JLabel ipLabel = new JLabel("Ip Address:");
        JTextField ipField = new JTextField();
    
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        
        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordField = new JTextField();
    
        JLabel socksPortLabel = new JLabel("Socks Port:");
        IntField socksPortField = new IntField(-1, 6);
    
        GroupLayout proxiesInfoLayout = new GroupLayout(detailsPanel);
        detailsPanel.setLayout(proxiesInfoLayout);
        proxiesInfoLayout.setAutoCreateGaps(true);
        proxiesInfoLayout.setAutoCreateContainerGaps(true);
    
        proxiesInfoLayout.setHorizontalGroup(proxiesInfoLayout.createSequentialGroup()
                        .addGroup(proxiesInfoLayout.createSequentialGroup()
                                .addGroup(proxiesInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(providerLabel)
                                        .addComponent(ipLabel)
                                        .addComponent(usernameLabel)
                                        .addComponent(passwordLabel)
                                        .addComponent(socksPortLabel))
        
                                .addGroup(proxiesInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(providerField)
                                        .addComponent(ipField)
                                        .addComponent(usernameField)
                                        .addComponent(passwordField)
                                    
                                        .addComponent(socksPortField))));
    
    
        proxiesInfoLayout.setVerticalGroup(proxiesInfoLayout.createSequentialGroup()
                .addGroup(proxiesInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(providerLabel)
                        .addComponent(providerField))
                .addGroup(proxiesInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(ipLabel)
                        .addComponent(ipField))
                .addGroup(proxiesInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(usernameLabel)
                        .addComponent(usernameField))
                .addGroup(proxiesInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField))
                .addGroup(proxiesInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(socksPortLabel)
                        .addComponent(socksPortField))
                
        );
        
        
        registerField(providerLabel, providerField, false, new ComplexFunction<>(Proxy::getProvider, Proxy::setProvider));
        registerField(ipLabel, ipField, false, new ComplexFunction<>(Proxy::getIpAddress, Proxy::setIpAddress));
        registerField(usernameLabel, usernameField, false, new ComplexFunction<>(Proxy::getUsername, Proxy::setUsername));
        registerField(passwordLabel, passwordField, false, new ComplexFunction<>(Proxy::getPassword, Proxy::setPassword));
        registerField(socksPortLabel, socksPortField, false, new ComplexFunction<>(Proxy::getSocksPort, Proxy::setSocksPort));
        
        
    }
    
    
    
    @Override
    public JPanel getDetailsPanel()
    {
        return detailsPanel;
    }
    
    @Override
    public List<Proxy> getData()
    {
        return getCore().getDatabase().getProxies().getAll();
    }
    
    
    @Override
    public JPanel getActionsPanel()
    {
        return null;
    }
    
    @Override
    public Proxy getBlankWithDefaultValues()
    {
        return new Proxy();
    }
    
    @Override
    public void updateSelection()
    {
    
    }
    
    @Override
    public void updateTable(Proxy saveObject)
    {
        PROXIES_MODEL.addElement(saveObject);
    }
    
    @Override
    public void saveToDatabase(Proxy saveObject)
    {
        getCore().getDatabase().getProxies().saveOrUpdate(saveObject);
    }
    
}
