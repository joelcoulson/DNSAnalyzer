package com.joelcoulson.dnsanalyzer.database;

import java.util.Set;

public class ClientDatabaseDAO {

    private static ClientDatabaseDAO clientDatabaseDAO;
    private ClientDatabase clients;

    public ClientDatabaseDAO() {
        clients = new ClientDatabase();
    }

    public static ClientDatabaseDAO getInststance() {
        synchronized (ClientDatabaseDAO.class) {
            if(clientDatabaseDAO == null) {
                clientDatabaseDAO = new ClientDatabaseDAO();
            }
            return clientDatabaseDAO;
        }
    }

    public void addClient(String ip, Client client) {
        if((ip.length() == 0) || (client == null)) { throw new IllegalArgumentException(); }
        synchronized (ClientDatabaseDAO.class) {
            clients.put(ip, client);
        }
    }

    public Client getClient(String ip) {
        if(ip.length() == 0) { throw new IllegalArgumentException(); }
        synchronized (ClientDatabaseDAO.class) {
            if(clients.containsKey(ip)) {
                return clients.get(ip);
            } else {
                return null;
            }
        }
    }

    public boolean clientExists(String ip) {
        if(ip.length() == 0) { throw new IllegalArgumentException(); }
        synchronized (ClientDatabaseDAO.class) {
            if(clients.containsKey(ip)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public Set<String> getClientIPs() {
        if(clients != null) {
            return clients.keySet();
        } else {
            return null;
        }
    }

    public void removeClient(String ip) { if(ip.length() == 0) { throw new IllegalArgumentException(); }
        synchronized (ClientDatabaseDAO.class) {
            clients.remove(ip);
        }
    }

    public void reset() {
        synchronized (ClientDatabaseDAO.class) {
            if(clients != null) {
                clients.clear();
            }
        }
    }
}
