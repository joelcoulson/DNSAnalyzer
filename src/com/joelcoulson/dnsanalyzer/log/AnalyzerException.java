package com.joelcoulson.dnsanalyzer.log;

import java.util.logging.*;

public class AnalyzerException extends Exception {

    private Log log = Log.getInstance();

    public AnalyzerException() {
        super();
    }

    public AnalyzerException(String message) {
        super(message);

        if(message.length() > 0) {
            log.log(Level.ALL, message);
        }
    }

    public AnalyzerException(Level level, String message) {
        super(message);

        if(level == Level.SEVERE) {
            System.out.println(message);
        }

        if(message.length() > 0) {
            log.log(Level.ALL, message);
        }
    }

}
