package com.joelcoulson.dnsanalyzer.capture;

public class Response {

    private long epoc;
    private String clientIP;
    private ResponseType responseType;

    public Response() {
        clientIP = "";
        responseType = null;
        setEpoc();
    }

    public Response(String ip, ResponseType responseType) {
        if(ip.length() == 0 || responseType == null) { throw new IllegalArgumentException(); }
        this.clientIP = ip;
        this.responseType = responseType;
        setEpoc();
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String ip) {
        if(ip.length() == 0) {throw new IllegalArgumentException();}
        this.clientIP = ip;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        if(responseType == null) {throw new IllegalArgumentException();}
        this.responseType = responseType;
    }

    public long getEpoc() {
        return this.epoc;
    }

    private void setEpoc() {
        this.epoc = System.currentTimeMillis()/1000;
    }
}
