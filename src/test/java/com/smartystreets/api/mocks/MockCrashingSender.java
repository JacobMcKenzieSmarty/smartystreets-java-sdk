package com.smartystreets.api.mocks;

import com.smartystreets.api.Request;
import com.smartystreets.api.Response;
import com.smartystreets.api.Sender;
import com.smartystreets.api.exceptions.SmartyException;
import com.smartystreets.api.exceptions.TooManyRequestsException;

import java.io.IOException;

public class MockCrashingSender implements Sender {
    private int sendCount = 0;
    private final int STATUS_CODE = 200;

    @Override
    public Response send(Request request) throws SmartyException, IOException {
        this.sendCount++;

        if (request.getUrl().contains("TooManyRequests")) {
            if (this.sendCount == 1) {
                throw new TooManyRequestsException("Too many requests. Sleeping...");
            }
        }

        if (request.getUrl().contains("RetryThreeTimes")) {
            if (this.sendCount <= 3) {
                throw new IOException("You need to retry");
            }
        }

        if (request.getUrl().contains("RetryMaxTimes")) {
            throw new IOException("Retrying won't help");
        }

        if (request.getUrl().contains("RetryFifteenTimes") ) {
            if (this.sendCount <= 14)
                throw new IOException("You need to retry");
        }

        return new Response(this.STATUS_CODE, new byte[]{});
    }

    public int getSendCount() {
        return this.sendCount;
    }
}
