package com.joelcoulson.dnsanalyzer.control;

import com.joelcoulson.dnsanalyzer.capture.Collector;
import com.joelcoulson.dnsanalyzer.config.Configuration;
import com.joelcoulson.dnsanalyzer.database.ClientDatabaseDAO;
import com.joelcoulson.dnsanalyzer.enforcement.Enforcer;
import com.joelcoulson.dnsanalyzer.log.AnalyzerException;
import com.joelcoulson.dnsanalyzer.queue.ResponseConsumer;

public class Controller {

    public Controller() {}

    public static void main(String args[]) {
        Configuration configuration = new Configuration();

        try {
            ClientDatabaseDAO clientDatabaseDAO = ClientDatabaseDAO.getInststance();

            // a new collector thread for each interface
            for(String iface : configuration.getListenerInterfaces()) {
                Thread collectorThread = new Thread(new Collector(iface, configuration.getCollectorWindow(), configuration.getDebugMode()));
                collectorThread.start();
            }

            // a new response consumer thread
            Thread responseConsumerThread = new Thread(new ResponseConsumer(clientDatabaseDAO, configuration.getDebugMode()));
            responseConsumerThread.start();

            // a new enforcer thread
            Thread enforcerThread = new Thread(new Enforcer(clientDatabaseDAO, configuration.getEnforcerWindow(), configuration.getEnforcementTimer(), configuration.getClientTrackingDuration(), configuration.getMinimumClientQueries(), configuration.getEnforcementRatio(), configuration.getDebugMode()));
            enforcerThread.start();

        }catch(AnalyzerException ae) {
            System.out.println("An exception was caught. Exiting.\nSee the system logs for more information.");
        }
    }

}
