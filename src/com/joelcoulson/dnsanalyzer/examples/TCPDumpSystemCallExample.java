package com.joelcoulson.dnsanalyzer.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TCPDumpSystemCallExample {

    public static void main(String[] args) {

        String s = null;

        try {

            Process p = Runtime.getRuntime().exec("tcpdump -n -c 100 udp port 53");
            p.waitFor();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            System.exit(0);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
