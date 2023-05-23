package core.gui;

import config.SharedConfig;
import core.Core;
import core.gui.tabs.BannedTab;
import core.gui.tabs.DashboardTab;
import core.gui.tabs.FreshTab;
import core.gui.tabs.MuleTab;
import core.gui.tabs.ProxiesTab;
import core.gui.tabs.WorkerTab;
import lombok.extern.log4j.Log4j2;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

@Log4j2
public class GUI
{
    private static final JTextPane consoleArea = new JTextPane();
    private static boolean invalidate = false;
    

    //ImageIcon hubIcon = new ImageIcon(ClassLoader.getSystemResource("\\images\\HubIcon.png"));
    
    
    ImageIcon hubIcon;
    JTabbedPane tabbedPane = new JTabbedPane();
    
    DashboardTab dashboard;
    WorkerTab workers;
    MuleTab mules;
    FreshTab fresh;
    BannedTab banned;
    ProxiesTab proxies;
    
    
    String programName = SharedConfig.SERVER_NAME;
    Core core;
    JFrame frame;

    public GUI(Core core)
    {
        this.core = core;
        if(this.core == null) System.out.println("Null core");
        
        URL hubURL = this.getClass().getResource("/images/HubIcon.png");
        if(hubURL != null)
        {
            hubIcon = new ImageIcon(hubURL);
        }
        
    }
    
    public void start() throws IOException
    {
        log.info("Starting...");
        frame = new JFrame();
        frame.setTitle(programName + " " + core.getVersion());
        frame.setIconImage(hubIcon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
            }
        });
        
        
        consoleArea.setEditable(false);
        consoleArea.setBackground(Color.DARK_GRAY);
        JPanel noWrapPane = new JPanel();
        noWrapPane.setLayout(new BorderLayout());
        noWrapPane.add(consoleArea);
        JScrollPane scrollPane = new JScrollPane(noWrapPane);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    
    
        tabbedPane = new JTabbedPane();
    
        dashboard = new DashboardTab(core);
        workers = new WorkerTab(tabbedPane, core);
        fresh = new FreshTab(tabbedPane, core);
        mules = new MuleTab(tabbedPane, core);
        banned = new BannedTab(tabbedPane, core);
        
        proxies = new ProxiesTab(tabbedPane, core);
        
        
        tabbedPane.add("Dashboard", dashboard.getPanel());
        tabbedPane.add("Workers", workers.getPanel());
        tabbedPane.add("Mules", mules.getPanel());
        tabbedPane.add("Fresh", fresh.getPanel());
        tabbedPane.add("Banned", banned.getPanel());
        tabbedPane.add("Proxies", proxies.getPanel());
        //tabbedPane.add("Console", scrollPane);
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new GridBagLayout());
    
        GridBagConstraints cTab = new GridBagConstraints();
        cTab.weighty = 1;
        cTab.weightx = 1;
        cTab.fill = GridBagConstraints.BOTH;
        cTab.gridx = 0;
        cTab.gridy = 0;
        cTab.gridheight = 1;
        mainPane.add(tabbedPane, cTab);
    
        frame.add(mainPane);
        frame.pack();
        frame.setSize(900, 850);
        frame.setLocation(150, 250);
        frame.setVisible(true);
        log.info("Started");
    }
    public void repaint()
    {
        if(invalidate)
        {
            frame.invalidate();
            update();
            invalidate = false;
        }
        frame.repaint();
    }
    
    public void update()
    {
        dashboard.update();
        banned.update();
        fresh.update();
        workers.update();
        mules.update();
    }
    
    public static synchronized void invalidate()
    {
        invalidate = true;
    }
    
    public static void appendToConsole(String string)
    {
        StyledDocument document = consoleArea.getStyledDocument();
        Style style = consoleArea.addStyle("MyStyle", null);
        StyleConstants.setForeground(style, Color.WHITE);
        StyleConstants.setFontSize(style, 14);
        try{
            document.insertString(document.getLength(), string + "\n", style);
        }catch(BadLocationException e)
        {
            log.error("Bad Location", e);
        }
    }
    
    public void stop()
    {
        frame.dispose();
    }
    
    public boolean isOpen()
    {
        return frame.isDisplayable();
    }
    
}
