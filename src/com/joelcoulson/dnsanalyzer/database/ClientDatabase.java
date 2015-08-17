package com.joelcoulson.dnsanalyzer.database;

import java.util.concurrent.ConcurrentHashMap;

// just a basic hashmap for now. theres a chance this may be changed to using
// a nosql database in the future
public class ClientDatabase extends ConcurrentHashMap<String, Client> {
}
