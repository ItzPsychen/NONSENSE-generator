package unipd.edids.logicBusiness.managers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerManager {

    private static LoggerManager instance;

    private LoggerManager() {
        // Costruttore privato
    }

    public static synchronized LoggerManager getInstance() {
        if (instance == null) {
            instance = new LoggerManager();
        }
        return instance;
    }

    public Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}
