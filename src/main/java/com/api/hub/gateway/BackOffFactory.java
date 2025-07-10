package com.api.hub.gateway;

import org.springframework.core.env.Environment;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

public class BackOffFactory {

    public static BackOff createBackOff(Environment environment, String prefix) {
        String type = environment.getProperty(prefix + ".backoff.type", "fixed").toLowerCase();

        switch (type) {
            case "exponential":
                long initialInterval = environment.getProperty(prefix + "backoff.exponential.initial-interval", Long.class, 2000L);
                long maxInterval = environment.getProperty(prefix + ".backoff.exponential.max-interval", Long.class, 30000L);
                double multiplier = environment.getProperty(prefix + ".backoff.exponential.multiplier", Double.class, 2.0);
                ExponentialBackOff exponentialBackOff = new ExponentialBackOff(initialInterval, multiplier);
                exponentialBackOff.setMaxInterval(maxInterval);
                return exponentialBackOff;

            case "fixed":
            default:
                long interval = environment.getProperty(prefix+ ".backoff.fixed.interval", Long.class, 5000L);
                long maxAttempts = environment.getProperty(prefix + ".backoff.fixed.max-attempts", Long.class, FixedBackOff.UNLIMITED_ATTEMPTS);
                FixedBackOff fixedBackOff = new FixedBackOff(interval, maxAttempts);
                return fixedBackOff;
        }
    }
}
