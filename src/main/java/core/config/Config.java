package core.config;

import java.util.concurrent.atomic.AtomicBoolean;

public class Config
{
    public static final int MAX_CLIENT_RAM = 512;
    public static final AtomicBoolean SHOULD_RUN = new AtomicBoolean(true);
    
    public static final String DREAMBOT_DIRECTORY = System.getProperty("user.home") + "\\DreamBot";
    public static final String JAVA_PATH = System.getProperty("java.home") + "\\bin\\java";
    public static String CLIENT_PATH = DREAMBOT_DIRECTORY + "\\BotData\\client.jar";
    public static String LAUNCHER_PATH = DREAMBOT_DIRECTORY + "\\Launcher\\DBLauncher.jar";
    public static String LOGS_DIRECTORY = DREAMBOT_DIRECTORY + "\\Logs";
    public static String MASTER_SCRIPT_NAME = "Master Script";
    
    public static final long CONNECTION_DELAY_SECONDS = 10;
    public static final int MAX_LAUNCHING_CLIENTS = 2;
    public static final int MAX_RUNNING_CLIENTS = 10;
    public static final String ACCOUNTS_FILE = "Accounts.account";
    public static final int MAX_WORKERS_PER_MULE = 10;
    public static final int TEMP_BAN_DAYS = 4;
    public static final int MIN_FRESH_ACCOUNTS = 2;
    public static final double TREKKER_SANFEW_RATIO = 6.0; //6 Trekkers to 1 Sanfew.
    
    public static final int MAX_WORKER_ACCOUNTS = (int) (Config.MAX_RUNNING_CLIENTS * 1.5);
}
