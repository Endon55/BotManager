package core.clients.sockets;

import config.PrivateSharedConfig;
import core.Core;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class SocketServer implements Runnable
{
    AtomicBoolean running = new AtomicBoolean(false);
    
    
    private final ServerSocket serverSocket;
    private Thread thread;
    private final Core core;
    
    private volatile Throwable exception;
    
    
    public SocketServer(Core core) throws IOException
    {
        log.info("Initializing...");
        this.core = core;
        this.serverSocket = new ServerSocket(PrivateSharedConfig.PORT);
    }
    @Override
    public void run()
    {
        running.set(true);
        while(running.get() && !serverSocket.isClosed())
        {
            try
            {
                Socket socket = serverSocket.accept();
                log.info("New Client Connected");
                Session session = new Session(socket, core.getClientManager());
                session.start();
                core.getClientManager().addSession(session);
            } catch(IOException e)
            {
                log.error("Fatal error while listening for new connections", e);
                closeServerSocket();
                
            }
        }
    }
    
    
    public void closeServerSocket()
    {
        try{
            serverSocket.close();
        } catch(IOException e)
        {
            log.error("Failed to close socket", e);
        }
    }
    
    public void start()
    {
        log.info("Listening for client to connect...");
        thread = new Thread(this);
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (th, ex) -> this.exception = ex;
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        thread.start();
    }
    
    public boolean hasCriticallyFailed()
    {
        return exception != null;
    }
    
    public Throwable getException()
    {
        return exception;
    }
    
    public void stop()
    {
        running.set(false);
        thread.interrupt();
        closeServerSocket();
    }
    
}
