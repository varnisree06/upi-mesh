package com.demo.upi_offline_mesh.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForwardingResultTest {

    @Test
    void shouldCreateSuccessfulResult() {

        ForwardingResult result =
                ForwardingResult.success();

        assertTrue(
                result.isSuccess()
        );

        assertNull(
                result.getFailureReason()
        );
    }


    @Test
    void shouldCreateFailedResult() {

        ForwardingResult result =
                ForwardingResult.failure(
                        FailureReason.NO_ROUTE
                );

        assertFalse(
                result.isSuccess()
        );

        assertEquals(
                FailureReason.NO_ROUTE,
                result.getFailureReason()
        );
    }
}