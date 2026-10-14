package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.FailureReason;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RetryDecisionServiceTest {

    @Test
    void shouldRetryWhenThereIsNoRoute() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertTrue(
                service.shouldRetry(
                        FailureReason.NO_ROUTE
                )
        );
    }


    @Test
    void shouldRetryWhenDeliveryFails() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertTrue(
                service.shouldRetry(
                        FailureReason.DELIVERY_FAILED
                )
        );
    }


    @Test
    void shouldNotRetryWhenTtlExpires() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertFalse(
                service.shouldRetry(
                        FailureReason.TTL_EXPIRED
                )
        );
    }


    @Test
    void shouldNotRetryDuplicatePacket() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertFalse(
                service.shouldRetry(
                        FailureReason.DUPLICATE_PACKET
                )
        );
    }


    @Test
    void shouldNotRetryDuplicatePayment() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertFalse(
                service.shouldRetry(
                        FailureReason.DUPLICATE_PAYMENT
                )
        );
    }


    @Test
    void shouldNotRetryInvalidPayment() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertFalse(
                service.shouldRetry(
                        FailureReason.INVALID_PAYMENT
                )
        );
    }


    @Test
    void shouldNotRetryExpiredPayment() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertFalse(
                service.shouldRetry(
                        FailureReason.PAYMENT_EXPIRED
                )
        );
    }


    @Test
    void shouldNotRetryInvalidState() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertFalse(
                service.shouldRetry(
                        FailureReason.INVALID_STATE
                )
        );
    }


    @Test
    void shouldNotRetryUnknownFailure() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertFalse(
                service.shouldRetry(
                        FailureReason.UNKNOWN
                )
        );
    }


    @Test
    void shouldNotRetryWhenFailureReasonIsNull() {

        RetryDecisionService service =
                new RetryDecisionService();

        assertFalse(
                service.shouldRetry(null)
        );
    }
}