package com.joelcoulson.dnsanalyzer.network;

import com.joelcoulson.dnsanalyzer.log.AnalyzerException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;

public class NetworkInterfaces {

    private Enumeration<NetworkInterface> inets;

    public NetworkInterfaces() {}

    public String getName(String interfaceIP) throws AnalyzerException {
        if(interfaceIP.length() == 0) { throw new IllegalArgumentException(); }
        initialize();
        if(inets == null) {
            return null;
        }

        for (NetworkInterface inet : Collections.list(inets)) {
            Enumeration<InetAddress> ips = inet.getInetAddresses();
            for(InetAddress ip : Collections.list(ips)) {
                if(ip.getHostAddress().equals(interfaceIP)) {
                    return inet.getName();
                }
            }
        }

        return null;
    }

    public ArrayList<String> getIPs(String interfaceName) throws AnalyzerException {
        if(interfaceName.length() == 0) { throw new IllegalArgumentException(); }
        initialize();
        if(inets == null) {
            return null;
        }

        ArrayList<String> ips = new ArrayList();

        for (NetworkInterface inet : Collections.list(inets)) {
            if(inet.getName().equals(interfaceName)) {
                Enumeration<InetAddress> addrs = inet.getInetAddresses();
                for(InetAddress addr : Collections.list(addrs)) {
                    ips.add(addr.getHostAddress());
                }
            }
        }

        return ips;
    }

    private void initialize() throws AnalyzerException {
        try {
            this.inets = NetworkInterface.getNetworkInterfaces();
        } catch(Exception e) {
            throw new AnalyzerException(Level.SEVERE, "Couldn't get a list of network adapters");
        }
    }

}
