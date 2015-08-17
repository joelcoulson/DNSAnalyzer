package com.joelcoulson.dnsanalyzer.queue;

import com.joelcoulson.dnsanalyzer.capture.Response;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.BlockingDeque;

public class ResponseQueue {

    private static BlockingDeque<Response> responseDeque;

    private ResponseQueue() {
        responseDeque = null;
    }

    public static BlockingDeque<Response> getInstance() {
        synchronized (ResponseQueue.class) {
            if(responseDeque == null) {
                responseDeque = new LinkedBlockingDeque();
            }
            return responseDeque;
        }
    }

}
