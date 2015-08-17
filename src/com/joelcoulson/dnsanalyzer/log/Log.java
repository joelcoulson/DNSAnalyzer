package com.joelcoulson.dnsanalyzer.log;

import com.joelcoulson.dnsanalyzer.config.Configuration;
import java.io.IOException;
import java.util.logging.*;

public class Log {

    private static Log log;
    private Logger logger;
    private FileHandler fileHandler;

    private Log() {
        try {
            logger = Logger.getLogger("DNS Analyzer");
            fileHandler = new FileHandler(new Configuration().getLogLocation());
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch(AnalyzerException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Log getInstance() {
        synchronized (Log.class) {
            if(log == null) {
                log = new Log();
            }
            return log;
        }
    }

    public void log(Level level, String message) {
        if(level == null || message.length() == 0) { throw new IllegalArgumentException(); }
        logger.log(level, message);
    }

}
