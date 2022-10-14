package com.lzy.learning.demo;
import io.sentry.Sentry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestSentryDemo {
    @Test
    void testSentry() {
        Sentry.init(options -> {
            options.setDsn("http://7ce2887b18204a18a91022540f4f76c1@localhost:9000/2");
            // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.setTracesSampleRate(1.0);
            // When first trying Sentry it's good to see what the SDK is doing:
            options.setDebug(true);
        });

        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
            Assertions.assertTrue(true);
        }
    }
}
