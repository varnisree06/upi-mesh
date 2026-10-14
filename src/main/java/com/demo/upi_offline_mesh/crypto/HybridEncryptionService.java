package com.demo.upi_offline_mesh.crypto;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.PublicKey;

@Service
public class HybridEncryptionService {

    private final ObjectMapper objectMapper;

    public HybridEncryptionService(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    public HybridEncryptedPayload encrypt(
            PaymentInstruction instruction,
            PublicKey receiverPublicKey
    ) throws Exception {

        // 1. Convert payment object to JSON
        String json =
                objectMapper.writeValueAsString(instruction);

        // 2. Generate a fresh AES session key
        SecretKey aesKey =
                AesGcmCrypto.generateKey();

        // 3. Encrypt the payment using AES-GCM
        String encryptedData =
                AesGcmCrypto.encrypt(
                        json,
                        aesKey
                );

        // 4. Convert AES key to Base64
        String encodedAesKey =
                java.util.Base64.getEncoder()
                        .encodeToString(
                                aesKey.getEncoded()
                        );

        // 5. Encrypt the AES key using RSA-OAEP
        String encryptedKey =
                RsaOaepCrypto.encrypt(
                        encodedAesKey,
                        receiverPublicKey
                );

        // 6. Return both pieces
        return new HybridEncryptedPayload(
                encryptedData,
                encryptedKey
        );
    }

    public PaymentInstruction decrypt(
            HybridEncryptedPayload payload,
            java.security.PrivateKey receiverPrivateKey
    ) throws Exception {

        // 1. Recover the AES key using RSA private key
        String encodedAesKey =
                RsaOaepCrypto.decrypt(
                        payload.getEncryptedKey(),
                        receiverPrivateKey
                );

        // 2. Convert Base64 back into AES key bytes
        byte[] aesKeyBytes =
                java.util.Base64.getDecoder()
                        .decode(encodedAesKey);

        // 3. Reconstruct AES SecretKey
        SecretKey aesKey =
                new javax.crypto.spec.SecretKeySpec(
                        aesKeyBytes,
                        "AES"
                );

        // 4. Decrypt the payment JSON
        String json =
                AesGcmCrypto.decrypt(
                        payload.getEncryptedData(),
                        aesKey
                );

        // 5. Convert JSON back to PaymentInstruction
        return objectMapper.readValue(
                json,
                PaymentInstruction.class
        );
    }
}