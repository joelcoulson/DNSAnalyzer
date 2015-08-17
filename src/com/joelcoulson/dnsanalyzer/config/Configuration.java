package com.joelcoulson.dnsanalyzer.config;

import com.joelcoulson.dnsanalyzer.log.AnalyzerException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Configuration {

    private final String propertiesFile = "./config/config.properties";
    private Properties properties;

    public Configuration() {
        try {
            properties = new Properties();
            FileInputStream inputStream = new FileInputStream(propertiesFile);
            properties.load(inputStream);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public boolean getDebugMode() throws AnalyzerException {
        String value = properties.getProperty("debug");
        boolean debug = false;

        if(value.equals("0")) {
            debug = false;
        } else if(value.equals("1")) {
            debug = true;
        } else {
            throw new AnalyzerException(Level.SEVERE, "Invalid debug mode in configuration");
        }

        return debug;
    }

    public String getLogLocation() throws AnalyzerException {
        String logLocation = properties.getProperty("log_location");

        if(logLocation.length() == 0) {
            throw new AnalyzerException(Level.SEVERE, "Invalid log location in configuration");
        }

        return logLocation;
    }

    public ArrayList<String> getListenerInterfaces() throws AnalyzerException {
        String values = properties.getProperty("interfaces");
        ArrayList<String> interfaces = new ArrayList();
        Pattern pattern = Pattern.compile(",");
        Scanner scanner = new Scanner(values);
        scanner.useDelimiter(",");

        while(scanner.hasNext()) {
            interfaces.add(scanner.next());
        }

        if(interfaces.size() == 0) {
            throw new AnalyzerException(Level.SEVERE, "Invalid interface list in configuration");
        }

        return interfaces;
    }

    public int getCollectorWindow() throws AnalyzerException {
        String value = properties.getProperty("collector_window");
        int collector = Integer.parseInt(value);

        if(!(collector > 0)) {
            throw new AnalyzerException(Level.SEVERE, "Invalid collector window duration in configuration");
        }

        return collector;
    }

    public int getEnforcerWindow() throws AnalyzerException {
        String value = properties.getProperty("enforcer_window");
        int enforcerWindow = Integer.parseInt(value);

        if(!(enforcerWindow > 0)) {
            throw new AnalyzerException(Level.SEVERE, "Invalid enforcer window duration in configuration");
        }

        return enforcerWindow;
    }

    public int getEnforcementTimer() throws AnalyzerException {
        String value = properties.getProperty("enforcement_timer");
        int enforcementTimer = Integer.parseInt(value);

        if(!(enforcementTimer > 0)) {
            throw new AnalyzerException(Level.SEVERE, "Invalid enforcement timer duration in configuration");
        }

        return enforcementTimer;
    }

    public int getClientTrackingDuration() throws AnalyzerException {
        String value = properties.getProperty("client_tracking_duration");
        int clientTrackingDuration = Integer.parseInt(value);

        if(!(clientTrackingDuration > 0)) {
            throw new AnalyzerException(Level.SEVERE, "Invalid client tracking duration in configuration");
        }

        return clientTrackingDuration;
    }

    public int getMinimumClientQueries() throws AnalyzerException {
        String value = properties.getProperty("minimum_client_queries");
        int minimumClientQueries = Integer.parseInt(value);

        if(!(minimumClientQueries > 0)) {
            throw new AnalyzerException(Level.SEVERE, "Invalid minimum client query number in configuration");
        }

        return minimumClientQueries;
    }

    public int getEnforcementRatio() throws AnalyzerException {
        String value = properties.getProperty("enforcement_ratio");
        int enforcementRatio = Integer.parseInt(value);

        if(!(enforcementRatio > 0)) {
            throw new AnalyzerException(Level.SEVERE, "Invalid enforcement ratio in configuration");
        }

        return enforcementRatio;
    }

    public String getSLBHost() throws AnalyzerException {
        String slbHost = properties.getProperty("slb_host");

        if(slbHost.length() == 0) {
            throw new AnalyzerException(Level.SEVERE, "Invalid SLB host in configuration");
        }

        return slbHost;
    }

    public String getSLBUser() throws AnalyzerException {
        String slbUser = properties.getProperty("slb_user");

        if(slbUser.length() == 0) {
            throw new AnalyzerException(Level.SEVERE, "Invalid SLB user in configuration");
        }

        return slbUser;
    }

    public String getSLBPassword() throws AnalyzerException {
        String slbPassword = properties.getProperty("slb_password");

        if(slbPassword.length() == 0) {
            throw new AnalyzerException(Level.SEVERE, "Invalid SLB password in configuration");
        }

        return slbPassword;
    }

    public String getSLBSuspensionGroup() throws AnalyzerException {
        String slbSuspensionGroup = properties.getProperty("slb_suspension_group");

        if(slbSuspensionGroup.length() == 0) {
            throw new AnalyzerException(Level.SEVERE, "Invalid SLB suspension group in configuration");
        }

        return slbSuspensionGroup;
    }

}
