package com.joelcoulson.dnsanalyzer.enforcement;

import com.joelcoulson.dnsanalyzer.database.*;
import com.joelcoulson.dnsanalyzer.log.Log;
import java.util.logging.Level;
import java.util.Set;

public class Enforcer implements Runnable {

    private Log log = Log.getInstance();
    private ClientDatabaseDAO runningClientDatabaseDAO;
    private ClientDatabaseDAO suspendedClientDatabaseDAO;
    private int enforcerWindow;
    private int enforcementTimer;
    private int clientTrackingDuration;
    private int minimumClientQueries;
    private int enforcementRatio;
    private boolean debug;

    public Enforcer() {
        this.runningClientDatabaseDAO = new ClientDatabaseDAO();
        this.suspendedClientDatabaseDAO = new ClientDatabaseDAO();
        this.enforcerWindow = 10000;
        this.enforcementTimer = 300;
        this.clientTrackingDuration = 300;
        this.minimumClientQueries = 500;
        this.enforcementRatio = 50;
    }

    public Enforcer(ClientDatabaseDAO clientDatabaseDAO, int enforcerWindow, int enforcementTimer, int clientTrackingDuration, int minimumClientQueries, int enforcementRatio, boolean debug) {
        if(clientDatabaseDAO == null || enforcerWindow == 0 || enforcementTimer == 0 || clientTrackingDuration == 0 || minimumClientQueries == 0 || enforcementRatio == 0) { throw new IllegalArgumentException(); }
        this.runningClientDatabaseDAO = clientDatabaseDAO;
        this.suspendedClientDatabaseDAO = new ClientDatabaseDAO();
        this.enforcerWindow = enforcerWindow;
        this.enforcementTimer = enforcementTimer;
        this.clientTrackingDuration = clientTrackingDuration;
        this.minimumClientQueries = minimumClientQueries;
        this.enforcementRatio = enforcementRatio;
        this.debug = debug;
    }

    private ClientDatabase getClientsToEnforce() {
        long currentEpoc = System.currentTimeMillis()/1000;
        ClientDatabase clientsToEnforce = new ClientDatabase();
        Set<String> clientIPs = runningClientDatabaseDAO.getClientIPs();
        Client client;

        // iterate through the database looking for clients to enforce
        for(String clientIP : clientIPs) {

            client = runningClientDatabaseDAO.getClient(clientIP);
            boolean removeClient = false;

            // flag old client data for removal
            if((currentEpoc - client.getEpoc()) > clientTrackingDuration) {
                removeClient = true;
            }

            // ensure we've got a minimal number of queries to analyze
            if(client.getTotalQueryCount() < minimumClientQueries) {
                if(debug == true) {
                    log.log(Level.INFO, "Client " + clientIP + " has too few responses to be analyzed");
                }
                continue;
            }

            // add to the enforcement list if client is over the ratio and remove from running database
            long clientRatio = (client.getBadQueryCount() / client.getGoodQueryCount()) * 100;
            if(clientRatio >= enforcementRatio) {
                if(debug == true) {
                    log.log(Level.INFO, "Client " + clientIP + " has a good:bad query ratio of " + clientRatio + " and WILL BE ENFORCED");
                }
                clientsToEnforce.put(clientIP, client);
                removeClient = true;
            } else {
                if(debug == true) {
                    log.log(Level.INFO, "Client " + clientIP + " has a good:bad query ratio of " + clientRatio + " and will not be enforced");
                }
            }

            // remove the clients record if its older than the enforcement window
            if(removeClient == true) {
                if(debug == true) {
                    log.log(Level.INFO, "Client " + clientIP + " record is older than enforcement window. Removing from database");
                }
                runningClientDatabaseDAO.removeClient(clientIP);
            }
        }

        return clientsToEnforce;
    }

    private void suspendClients(ClientDatabase clientDatabase) {
        if(clientDatabase.size() > 0) {
            Set<String> clientIPs = clientDatabase.keySet();
            for(String clientIP : clientIPs) {
                // TODO: Create an SLB class that'll be used in the suspension of the clients
                Client client = clientDatabase.get(clientIP);
                client.initializeEpoc();    // reset the epoc. this will be used to unsuspend the client
                suspendedClientDatabaseDAO.addClient(clientIP, client);
                log.log(Level.INFO, clientIP + " has been suspended");
            }
        }
    }

    private void unsuspendClients() {
        long currentEpoc = System.currentTimeMillis()/1000;
        Set<String> clientIPs = suspendedClientDatabaseDAO.getClientIPs();

        // unsuspend clients that have exceeded their enforcement timer
        if(clientIPs.size() > 0) {
            for(String clientIP : clientIPs) {
                // TODO: Create an SLB class that'll be used in the suspension of the clients
                Client client = suspendedClientDatabaseDAO.getClient(clientIP);
                if((currentEpoc - client.getEpoc()) > enforcementTimer) {
                    suspendedClientDatabaseDAO.removeClient(clientIP);
                    log.log(Level.INFO, clientIP + " has been unsuspended");
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            while(true) {
                ClientDatabase clientsToEnforce = getClientsToEnforce();
                suspendClients(clientsToEnforce);
                unsuspendClients();
                if(debug == true) {
                    log.log(Level.INFO, "Enforcer is sleeping for " + enforcerWindow + " milliseconds");
                }
                Thread.sleep(enforcerWindow);
            }
        } catch(InterruptedException ie) {
        }
    }
}
