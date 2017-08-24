import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 */
public class MyLogger {
    public static final MyLogger _instance = new MyLogger();
    public static final String LOG_PATH = "/home/meni/Desktop/ML/logs/";

    Logger logger = Logger.getLogger("MyLog");
    FileHandler fh;

    private MyLogger() {
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(LOG_PATH+System.currentTimeMillis()+".log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MyLogger getInstance() {
        return _instance;
    }

    public static void info(String text) {
        getInstance().logger.info(text);
    }

    public static void warning(String text) {
        getInstance().logger.warning(text);
    }

    public static void fine(String text) {
        getInstance().logger.fine(text);
    }

}
