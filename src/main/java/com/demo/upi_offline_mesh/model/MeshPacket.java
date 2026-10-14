package com.demo.upi_offline_mesh.model;

import java.time.Instant;
import java.util.UUID;

public class MeshPacket {

    private UUID packetId;

    private String origin;

    private int ttl;

    private Instant createdAt;

    private String encryptedData;

    private String encryptedKey;

    private String signature;

    private String destination;

    public MeshPacket() {
    }

    public MeshPacket(
            UUID packetId,
            String origin,
            String destination,
            int ttl,
            Instant createdAt,
            String encryptedData,
            String encryptedKey,
            String signature
    ) {
        this.packetId = packetId;
        this.origin = origin;
        this.destination = destination;
        this.ttl = ttl;
        this.createdAt = createdAt;
        this.encryptedData = encryptedData;
        this.encryptedKey = encryptedKey;
        this.signature = signature;
    }

    public UUID getPacketId() {
        return packetId;
    }

    public void setPacketId(UUID packetId) {
        this.packetId = packetId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}