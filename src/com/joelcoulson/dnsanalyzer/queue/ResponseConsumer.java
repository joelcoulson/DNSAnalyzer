package com.joelcoulson.dnsanalyzer.queue;

import com.joelcoulson.dnsanalyzer.capture.*;
import com.joelcoulson.dnsanalyzer.database.*;
import com.joelcoulson.dnsanalyzer.log.Log;
import java.util.Deque;
import java.util.logging.Level;

public class ResponseConsumer implements Runnable {

    private Deque<Response> responseQueue = ResponseQueue.getInstance();
    private ClientDatabaseDAO clientDatabaseDAO;
    private Log log = Log.getInstance();
    private boolean debug;

    public ResponseConsumer(ClientDatabaseDAO clientDatabaseDAO) {
        if(clientDatabaseDAO == null) { throw new IllegalArgumentException(); }
        this.clientDatabaseDAO = clientDatabaseDAO;
        this.debug = false;
    }

    public ResponseConsumer(ClientDatabaseDAO clientDatabaseDAO, boolean debug) {
        if(clientDatabaseDAO == null) { throw new IllegalArgumentException(); }
        this.clientDatabaseDAO = clientDatabaseDAO;
        this.debug = debug;
    }

    @Override
    public void run() {
        // continue to consume responses from the queue until killed
        while(true) {
            try {
                if (responseQueue.size() > 0) {
                    processResponse(responseQueue.getFirst());
                    responseQueue.removeFirst();
                } else {
                    // queue is empty. wait a second before trying again
                    if (debug == true) {
                        log.log(Level.INFO, "Response queue empty. Consumer is sleeping for 1000 milliseconds");
                    }
                    Thread.sleep(1000);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processResponse(Response response) {
        if(response == null) {
            throw new IllegalArgumentException();
        }

        String ip = response.getClientIP();
        Client client;

        if(clientDatabaseDAO.clientExists(ip)) {
            client = clientDatabaseDAO.getClient(ip);
        } else {
            if (debug == true) {
                log.log(Level.INFO, "Client " + ip + " does not exist in database. Adding");
            }
            client = new Client();
            clientDatabaseDAO.addClient(ip, client);
        }

        switch(response.getResponseType()) {
            case NXDomain:
                if (debug == true) {
                    log.log(Level.INFO, "Incrementing good query count for " + ip);
                }
                client.setBadQueryCount(client.getBadQueryCount()+1);
                break;
            default:
                if (debug == true) {
                    log.log(Level.INFO, "Incrementing bad query count for " + ip);
                }
                client.setGoodQueryCount(client.getGoodQueryCount()+1);
        }
    }
}
