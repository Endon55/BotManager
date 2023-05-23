package core.gui.tabs;

import core.Core;
import core.clients.ClientManager;
import core.gui.GUI;
import lombok.extern.log4j.Log4j2;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
@Log4j2
public class DashboardTab implements ITab
{
    JPanel dashboardPanel;
    JLabel connectedClientsValue;
    JLabel todaysProfitValue;
    
    Core core;
    public DashboardTab(Core core)
    {
        this.core = core;
    }
    
    @Override
    public JPanel getPanel()
    {
        dashboardPanel = new JPanel();
        
        //Toggle Launch
        JLabel launchClientsLabel = new JLabel("Launch Clients: ");
        JLabel launchClientsValue;
        if(ClientManager.getAutoLaunchClients())
        {
            launchClientsValue = new JLabel("Enabled");
            log.info("Scheduler enabled");
        }
        else
        {
            launchClientsValue = new JLabel("Disabled");
            log.info("Scheduler disabled");
        }
        JButton toggleLaunchButton = new JButton("Toggle");
        toggleLaunchButton.setMargin(new Insets(0, 0, 0, 0));
        toggleLaunchButton.setPreferredSize(new Dimension(50, 15));
        toggleLaunchButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(ClientManager.getAutoLaunchClients())
                {
                    ClientManager.setAutoLaunchClients(false);
                    launchClientsValue.setText("Disabled");
                    log.info("Scheduler: enabled -> disabled");
                }
                else{
                    ClientManager.setAutoLaunchClients(true);
                    launchClientsValue.setText("Enabled");
                    log.info("Scheduler: disabled -> enabled");
                }
            }
        });
        
        //Connected Clients
        JLabel connectedClientsLabel = new JLabel("Connected Clients: ");
        connectedClientsValue = new JLabel("" + ClientManager.activeConnections());
        
        //Todays Profits
        JLabel todaysProfitsLabel = new JLabel("Today's Profit: ");
        todaysProfitValue = new JLabel("" + core.getDatabase().getAccountProvisioner().getTodaysProfit());
    
        //Fresh Accounts
        JLabel freshAccountsLabel = new JLabel("Fresh Accounts: ");
        JButton importAccountsButton = getImportFreshButton();
        JButton exportAccountsButton = getExportFreshButton();
    
    
        JLabel refreshLabel = new JLabel("Refresh: ");
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GUI.invalidate();
            }
        });
        
        GroupLayout dashboardLayout = new GroupLayout(dashboardPanel);
        dashboardPanel.setLayout(dashboardLayout);
        dashboardLayout.setAutoCreateGaps(true);
        dashboardLayout.setAutoCreateContainerGaps(true);
        
 
        
        
        dashboardLayout.setHorizontalGroup(dashboardLayout.createSequentialGroup()
                .addGroup(dashboardLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(dashboardLayout.createSequentialGroup()
                                .addGroup(dashboardLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(launchClientsLabel)
                                        .addComponent(connectedClientsLabel)
                                        .addComponent(freshAccountsLabel)
                                        .addComponent(todaysProfitsLabel)
                                        .addComponent(refreshLabel))
                                .addGroup(dashboardLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(launchClientsValue)
                                        .addComponent(connectedClientsValue)
                                        .addComponent(importAccountsButton)
                                        .addComponent(todaysProfitValue)
                                        .addComponent(refreshButton))
                                .addGroup(dashboardLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(toggleLaunchButton)
                                        
                                        .addComponent(exportAccountsButton))
                        )));
    
        dashboardLayout.setVerticalGroup(dashboardLayout.createSequentialGroup()
                        .addGroup(dashboardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(launchClientsLabel)
                                        .addComponent(launchClientsValue)
                                        .addComponent(toggleLaunchButton))
                        .addGroup(dashboardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(connectedClientsLabel)
                                .addComponent(connectedClientsValue))
                        .addGroup(dashboardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(freshAccountsLabel)
                                .addComponent(importAccountsButton)
                                .addComponent(exportAccountsButton))
                        .addGroup(dashboardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(todaysProfitsLabel)
                        .addComponent(todaysProfitValue))
                .addGroup(dashboardLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(refreshLabel)
                        .addComponent(refreshButton)));
        dashboardLayout.linkSize(toggleLaunchButton, importAccountsButton, exportAccountsButton);


        return this.dashboardPanel;
    }
    
    
    private JButton getImportFreshButton()
    {
        JButton importButton = new JButton("Import");
        importButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                core.getDatabase().getAccountProvisioner().importFreshAccounts();
            }
        });
        return importButton;
    }
    
    private JButton getExportFreshButton()
    {
        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int result = JOptionPane.showConfirmDialog(exportButton, "Wipe exported accounts from local database?", "Export Fresh Accounts",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    
                if(result == 0) //Yes
                {
                    core.getDatabase().getAccountProvisioner().exportFreshAccounts(true);
                }
                else if(result == 1) //No
                {
                    core.getDatabase().getAccountProvisioner().exportFreshAccounts(false);
                }
            }
        });
        return exportButton;
    }
    
    
    public void update()
    {
        connectedClientsValue.setText("" + ClientManager.activeConnections());
        todaysProfitValue.setText("" + core.getDatabase().getAccountProvisioner().getTodaysProfit());
    }
    
    
}
