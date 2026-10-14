package com.demo.upi_offline_mesh.model;

public class ForwardingResult {

    private final boolean success;

    private final FailureReason failureReason;


    private ForwardingResult(
            boolean success,
            FailureReason failureReason
    ) {
        this.success = success;
        this.failureReason = failureReason;
    }


    public static ForwardingResult success() {

        return new ForwardingResult(
                true,
                null
        );
    }


    public static ForwardingResult failure(
            FailureReason reason
    ) {

        return new ForwardingResult(
                false,
                reason
        );
    }


    public boolean isSuccess() {

        return success;
    }


    public FailureReason getFailureReason() {

        return failureReason;
    }
}