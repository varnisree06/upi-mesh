package com.demo.upi_offline_mesh.model;

import java.time.Instant;
import java.util.UUID;

public class PaymentInstruction {

    private UUID paymentId;

    private Long senderId;

    private Long receiverId;

    private long amount;

    private String currency;

    private Instant createdAt;

    private String nonce;

    private TransactionStatus status;

    public PaymentInstruction() {
    }

    public PaymentInstruction(
            UUID paymentId,
            Long senderId,
            Long receiverId,
            long amount,
            String currency,
            Instant createdAt,
            String nonce

    ) {
        this.paymentId = paymentId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.currency = currency;
        this.createdAt = createdAt;
        this.nonce = nonce;
        this.status = TransactionStatus.CREATED;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}