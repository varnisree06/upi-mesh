package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.ForwardingResult;
import com.demo.upi_offline_mesh.model.MeshPacket;
import org.springframework.stereotype.Service;

@Service
public class RetryService {

    private final MeshForwardingService forwardingService;
    private final RetryDecisionService retryDecisionService;

    public RetryService(
            MeshForwardingService forwardingService,
            RetryDecisionService retryDecisionService
    ) {
        this.forwardingService = forwardingService;
        this.retryDecisionService = retryDecisionService;
    }

    public ForwardingResult forwardWithRetry(
            String startingNode,
            MeshPacket packet,
            int maxAttempts
    ) {

        if (maxAttempts <= 0) {
            throw new IllegalArgumentException(
                    "maxAttempts must be greater than zero"
            );
        }

        ForwardingResult result = null;

        for (int attempt = 1;
             attempt <= maxAttempts;
             attempt++) {

            result =
                    forwardingService.forward(
                            startingNode,
                            packet
                    );

            // Successfully forwarded.
            if (result.isSuccess()) {
                return result;
            }

            // Failure is not retryable.
            if (!retryDecisionService.shouldRetry(
                    result.getFailureReason()
            )) {
                return result;
            }
        }

        // Maximum attempts exhausted.
        return result;
    }
}