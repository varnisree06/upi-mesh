package com.demo.upi_offline_mesh.crypto;

public class HybridEncryptedPayload {

    private String encryptedData;
    private String encryptedKey;

    public HybridEncryptedPayload() {
    }

    public HybridEncryptedPayload(
            String encryptedData,
            String encryptedKey
    ) {
        this.encryptedData = encryptedData;
        this.encryptedKey = encryptedKey;
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
}