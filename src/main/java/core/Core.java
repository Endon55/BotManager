package core;

import core.clients.ClientManager;
import core.clients.sockets.SocketServer;
import core.config.Config;
import core.database.Database;
import core.gui.GUI;
import core.utilities.Utils;
import logging.Logger;
import lombok.extern.log4j.Log4j2;
import tools.utils.NullSafe;

import java.io.IOException;

@Log4j2
public class Core
{
    private final ClientManager clientManager;
    private final SocketServer socketServer;
    private final GUI gui;
    private final Database database;
    
    public Core() throws IOException
    {
        database = new Database();
        
        clientManager = new ClientManager(database,   true);
        
        socketServer = new SocketServer(this);
        socketServer.start();
        
        gui = new GUI(this);
        gui.start();
        
        
        
    }
    
    public void run()
    {
        while(Config.SHOULD_RUN.get())
        {
            if(gui.isOpen())
            {
                gui.repaint();
            }
            else
            {
                Config.SHOULD_RUN.set(false);
            }
            
            if(socketServer.hasCriticallyFailed())
            {
                log.error(socketServer.getException());
                Config.SHOULD_RUN.set(false);
            }
            
            database.getAccountProvisioner().provision();
            clientManager.tick();
            Utils.sleep(150);
        }
    
        Logger.log("Shutting down.");
        
        stop();
    }
    public String getVersion()
    {
        return NullSafe.of(getClass().getPackage().getImplementationVersion()).getOrDefault("^.^");
    }
    

    public void errorStop(String message)
    {
        log.error(message);
        stop();
    }
    public void stop()
    {
        database.close();
        socketServer.stop();
        //gui.stop();
    }
    
    public Database getDatabase()
    {
        return database;
    }
    public ClientManager getClientManager()
    {
        return clientManager;
    }

}
