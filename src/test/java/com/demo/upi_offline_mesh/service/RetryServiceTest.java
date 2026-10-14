package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.FailureReason;
import com.demo.upi_offline_mesh.model.ForwardingResult;
import com.demo.upi_offline_mesh.model.MeshPacket;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RetryServiceTest {

    @Test
    void shouldReturnImmediatelyWhenForwardingSucceeds() {

        MeshForwardingService forwardingService =
                mock(MeshForwardingService.class);

        RetryDecisionService retryDecisionService =
                mock(RetryDecisionService.class);

        RetryService retryService =
                new RetryService(
                        forwardingService,
                        retryDecisionService
                );

        MeshPacket packet =
                createPacket();


        ForwardingResult success =
                ForwardingResult.success();


        when(
                forwardingService.forward(
                        anyString(),
                        any(MeshPacket.class)
                )
        ).thenReturn(success);


        ForwardingResult result =
                retryService.forwardWithRetry(
                        "A",
                        packet,
                        3
                );


        assertTrue(
                result.isSuccess()
        );


        verify(
                forwardingService,
                times(1)
        ).forward(
                "A",
                packet
        );
    }


    @Test
    void shouldRetryWhenFailureIsRetryable() {

        MeshForwardingService forwardingService =
                mock(MeshForwardingService.class);

        RetryDecisionService retryDecisionService =
                mock(RetryDecisionService.class);

        RetryService retryService =
                new RetryService(
                        forwardingService,
                        retryDecisionService
                );

        MeshPacket packet =
                createPacket();


        ForwardingResult failure =
                ForwardingResult.failure(
                        FailureReason.NO_ROUTE
                );


        ForwardingResult success =
                ForwardingResult.success();


        when(
                forwardingService.forward(
                        anyString(),
                        any(MeshPacket.class)
                )
        )
                .thenReturn(
                        failure,
                        success
                );


        when(
                retryDecisionService.shouldRetry(
                        FailureReason.NO_ROUTE
                )
        ).thenReturn(true);


        ForwardingResult result =
                retryService.forwardWithRetry(
                        "A",
                        packet,
                        3
                );


        assertTrue(
                result.isSuccess()
        );


        verify(
                forwardingService,
                times(2)
        ).forward(
                "A",
                packet
        );


        verify(
                retryDecisionService,
                times(1)
        ).shouldRetry(
                FailureReason.NO_ROUTE
        );
    }


    @Test
    void shouldStopWhenFailureIsNotRetryable() {

        MeshForwardingService forwardingService =
                mock(MeshForwardingService.class);

        RetryDecisionService retryDecisionService =
                mock(RetryDecisionService.class);

        RetryService retryService =
                new RetryService(
                        forwardingService,
                        retryDecisionService
                );

        MeshPacket packet =
                createPacket();


        ForwardingResult failure =
                ForwardingResult.failure(
                        FailureReason.TTL_EXPIRED
                );


        when(
                forwardingService.forward(
                        anyString(),
                        any(MeshPacket.class)
                )
        ).thenReturn(failure);


        when(
                retryDecisionService.shouldRetry(
                        FailureReason.TTL_EXPIRED
                )
        ).thenReturn(false);


        ForwardingResult result =
                retryService.forwardWithRetry(
                        "A",
                        packet,
                        3
                );


        assertFalse(
                result.isSuccess()
        );


        assertEquals(
                FailureReason.TTL_EXPIRED,
                result.getFailureReason()
        );


        verify(
                forwardingService,
                times(1)
        ).forward(
                "A",
                packet
        );


        verify(
                retryDecisionService,
                times(1)
        ).shouldRetry(
                FailureReason.TTL_EXPIRED
        );
    }


    @Test
    void shouldStopAfterMaximumAttempts() {

        MeshForwardingService forwardingService =
                mock(MeshForwardingService.class);

        RetryDecisionService retryDecisionService =
                mock(RetryDecisionService.class);

        RetryService retryService =
                new RetryService(
                        forwardingService,
                        retryDecisionService
                );

        MeshPacket packet =
                createPacket();


        ForwardingResult failure =
                ForwardingResult.failure(
                        FailureReason.NO_ROUTE
                );


        when(
                forwardingService.forward(
                        anyString(),
                        any(MeshPacket.class)
                )
        ).thenReturn(failure);


        when(
                retryDecisionService.shouldRetry(
                        FailureReason.NO_ROUTE
                )
        ).thenReturn(true);


        ForwardingResult result =
                retryService.forwardWithRetry(
                        "A",
                        packet,
                        3
                );


        assertFalse(
                result.isSuccess()
        );


        assertEquals(
                FailureReason.NO_ROUTE,
                result.getFailureReason()
        );


        verify(
                forwardingService,
                times(3)
        ).forward(
                "A",
                packet
        );
    }


    @Test
    void shouldRejectZeroMaximumAttempts() {

        MeshForwardingService forwardingService =
                mock(MeshForwardingService.class);

        RetryDecisionService retryDecisionService =
                mock(RetryDecisionService.class);

        RetryService retryService =
                new RetryService(
                        forwardingService,
                        retryDecisionService
                );


        assertThrows(
                IllegalArgumentException.class,
                () -> retryService.forwardWithRetry(
                        "A",
                        createPacket(),
                        0
                )
        );


        verifyNoInteractions(
                forwardingService,
                retryDecisionService
        );
    }


    private MeshPacket createPacket() {

        return new MeshPacket(
                UUID.randomUUID(),
                "A",
                "BRIDGE",
                5,
                Instant.now(),
                "encrypted-data",
                "encrypted-key",
                "signature"
        );
    }
}