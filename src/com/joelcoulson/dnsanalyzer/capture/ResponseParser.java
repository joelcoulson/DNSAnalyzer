package com.joelcoulson.dnsanalyzer.capture;

import com.joelcoulson.dnsanalyzer.log.Log;
import com.joelcoulson.dnsanalyzer.queue.ResponseQueue;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseParser implements Runnable {

    private boolean debug;
    private Log log = Log.getInstance();

    public ResponseParser(ArrayList<String> lines, boolean debug) {
        this.debug = debug;
        parseResponse(lines);
    }

    private void parseResponse(ArrayList<String> lines) {
        if(lines == null) { throw new IllegalArgumentException(); }
        // takes the output from tcpdump and parses into response objects which are then placed onto the queue

        // in the interests of performance, we're only going to match the common responses
        Pattern pattern = Pattern.compile("\\sA\\s|\\sAAAA\\s|\\sCNAME\\s|\\sMX\\s|\\sPTR\\s|\\sTXT\\s|\\sNXDomain\\s|\\sServFail\\s");
        Matcher matcher = null;

        for(String line : lines) {
            ResponseType responseType;
            matcher = pattern.matcher(line);
            String clientIP = getClientIP(line);

            if(clientIP == null) {
                continue;
            }

            if(matcher.find()) {
                switch(matcher.group()) {
                    case " A ":
                        responseType = ResponseType.A;
                        break;
                    case " AAAA ":
                        responseType = ResponseType.AAAA;
                        break;
                    case " CNAME ":
                        responseType = ResponseType.CNAME;
                        break;
                    case " MX ":
                        responseType = ResponseType.MX;
                        break;
                    case " PTR ":
                        responseType = ResponseType.PTR;
                        break;
                    case " TXT ":
                        responseType = ResponseType.TXT;
                        break;
                    case " NXDomain ":
                        responseType = ResponseType.NXDomain;
                        break;
                    case " ServFail ":
                        responseType = ResponseType.ServFail;
                        break;
                    default:
                        responseType = ResponseType.A;
                }

                if (debug == true) {
                    log.log(Level.INFO, "Observed " + responseType + " query type for " + clientIP);
                }

                ResponseQueue.getInstance().add(new Response(clientIP, responseType));
            }
        }
    }

    private String getClientIP(String line) {
        if(line == null || line.length() == 0) { throw new IllegalArgumentException(); }
        // fifth token will always be the client (ipv4 or ipv6) given our tcpdump filter
        // ie: 16:46:15.120392 IP 203.0.178.191.53 > 203.59.141.7.4230: 48544 1/0/0 A 218.14.146.201 (47)
        String ip = line.split("\\s")[4];
        Pattern pattern = Pattern.compile("(([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)|(([a-z]|[0-9])+:([a-z]|[0-9])+:([a-z]|[0-9])+:([a-z]|[0-9])+:([a-z]|[0-9])+:([a-z]|[0-9])+:([a-z]|[0-9])+:([a-z]|[0-9])+))");
        Matcher matcher = pattern.matcher(ip);
        if(matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    @Override
    public void run() {}
}
