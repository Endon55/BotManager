package core.gui.impl;

import core.clients.ClientManager;
import core.clients.ConnectionStatus;
import core.database.tables.accounts.Account;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Component;
import java.util.Objects;

public class StatusListRenderer extends DefaultListCellRenderer
{
    Icon connected = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/TinyGreenCheck.png")));
    Icon connecting = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/TinyYellowCircle.png")));
    Icon sleeping = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/TinySleep.png")));
    Icon disconnected = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/TinyRedX.png")));
    ClientManager manager;
    
    public StatusListRenderer(ClientManager manager)
    {
        this.manager = manager;
    }
    
    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus)
    {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        
         if(value instanceof Account)
        {
           ConnectionStatus status = manager.getConnectionStatus(((Account) value).getId());
            if(status == ConnectionStatus.CONNECTED)
            {
                label.setIcon(connected);
                label.setHorizontalTextPosition(JLabel.RIGHT);
            }
            else if(status == ConnectionStatus.CONNECTING)
            {
                label.setIcon(connecting);
                label.setHorizontalTextPosition(JLabel.RIGHT);
            }
            else if(status == ConnectionStatus.SLEEPING)
            {
                label.setIcon(sleeping);
                label.setHorizontalTextPosition(JLabel.RIGHT);
            }
            else
            {
                label.setIcon(disconnected);
                label.setHorizontalTextPosition(JLabel.RIGHT);
            }
            label.setText(((Account) value).getDisplayName());
        }
        //label.setFont(font);
        return label;
    }
}
