package com.joelcoulson.dnsanalyzer.capture;

import com.joelcoulson.dnsanalyzer.log.AnalyzerException;
import com.joelcoulson.dnsanalyzer.log.Log;
import com.joelcoulson.dnsanalyzer.network.NetworkInterfaces;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Collector implements Runnable {

    private String iface;
    private int collectorWindow;
    private Log log = Log.getInstance();
    private boolean debug;

    public Collector() {
        this.iface = "eth0";
        this.collectorWindow = 100;
        this.debug = false;
    }

    public Collector(String iface, int collectorWindow, boolean debug) {
        this.iface = iface;
        this.collectorWindow = collectorWindow;
        this.debug = debug;
    }

    public void run() {
        ArrayList<String> responses = new ArrayList();
        StringBuilder serverIPs = new StringBuilder();
        String line = null;

        // we're only going to listen to responses that come from servers IP to ensure we capture only the responses
        // sent back to the clients, rather than response from the auth servers to the recursor
        try {
            Pattern pattern = Pattern.compile("%" + iface);
            ArrayList<String> ips = new NetworkInterfaces().getIPs(iface);

            for(int i = 0; i < ips.size(); i++) {
                // strip the %<interface string> from ipv6 addresses
                Matcher matcher = pattern.matcher(ips.get(i));
                ips.set(i, matcher.replaceAll(""));

                serverIPs.append("src " + ips.get(i));
                if(i != (ips.size()-1)) {
                    serverIPs.append(" or ");
                }
            }
        } catch(AnalyzerException ae) {
            log.log(Level.SEVERE, "Couldn't get a list of system adapters");
        }

        // collect forever
        while(true) {

            try {
                Process p = Runtime.getRuntime().exec("tcpdump " + serverIPs + " -n -c " + collectorWindow + " and udp port 53");
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                // read the output from the tcpdump
                while ((line = stdInput.readLine()) != null) {
                    responses.add(line);
                }

                // parse the capture and place them onto the queue
                Thread thread = new Thread(new ResponseParser(responses, debug));
                thread.start();
                responses.clear();

            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}