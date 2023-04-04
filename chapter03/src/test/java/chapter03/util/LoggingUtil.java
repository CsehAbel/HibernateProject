package chapter03.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LoggingUtil {

    // C:\Users\z004a6nh\IdeaProjects\HibernateProject\chapter03\reports
    public static String path = "C:\\Users\\z004a6nh\\IdeaProjects\\HibernateProject\\chapter03\\reports";

    public static Logger initLogger(String name, String filename) {
        Logger logger = Logger.getLogger("lel");
        try {
            FileHandler handler = new FileHandler(filename);
            handler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord lr) {
                    return lr.getLevel() + ": " + lr.getMessage() + "\r\n";
                }
            });
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }
}
