package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.FailureReason;
import org.springframework.stereotype.Service;

@Service
public class RetryDecisionService {

    public boolean shouldRetry(
            FailureReason failureReason
    ) {

        if (failureReason == null) {
            return false;
        }

        return switch (failureReason) {

            /*
             * These failures may be temporary.
             *
             * The network can recover later.
             */
            case NO_ROUTE,
                 DELIVERY_FAILED ->
                    true;


            /*
             * These failures should NOT be retried.
             */
            case TTL_EXPIRED,
                 DUPLICATE_PACKET,
                 DUPLICATE_PAYMENT,
                 INVALID_PAYMENT,
                 PAYMENT_EXPIRED,
                 INVALID_STATE,
                 UNKNOWN ->
                    false;
        };
    }
}