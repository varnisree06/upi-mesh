package com.demo.upi_offline_mesh.model;

public enum FailureReason {

    NO_ROUTE,

    TTL_EXPIRED,

    DUPLICATE_PACKET,

    DUPLICATE_PAYMENT,

    INVALID_PAYMENT,

    PAYMENT_EXPIRED,

    DELIVERY_FAILED,

    INVALID_STATE,

    UNKNOWN
}