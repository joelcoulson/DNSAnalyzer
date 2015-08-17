package com.joelcoulson.dnsanalyzer.database;

public class Client {

    private long epoc;
    private long goodQueryCount;
    private long badQueryCount;

    public Client() {
        initializeEpoc();
        setGoodQueryCount(1); // prevent a divide by zero issue
        setBadQueryCount(1);
    }

    public void initializeEpoc() {
        epoc = System.currentTimeMillis()/1000;
    }

    public long getGoodQueryCount() {
        return goodQueryCount;
    }

    public void setGoodQueryCount(long goodQueryCount) {
        this.goodQueryCount = goodQueryCount;
    }

    public long getBadQueryCount() {
        return badQueryCount;
    }

    public void setBadQueryCount(long badQueryCount) {
        this.badQueryCount = badQueryCount;
    }

    public long getTotalQueryCount() {
        return goodQueryCount + badQueryCount;
    }

    public long getEpoc() {
        return this.epoc;
    }

}
