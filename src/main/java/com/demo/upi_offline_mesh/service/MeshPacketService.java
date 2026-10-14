package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.crypto.HybridEncryptedPayload;
import com.demo.upi_offline_mesh.crypto.HybridEncryptionService;
import com.demo.upi_offline_mesh.crypto.RsaSignatureCrypto;
import com.demo.upi_offline_mesh.model.MeshPacket;
import com.demo.upi_offline_mesh.model.PaymentInstruction;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;

@Service
public class MeshPacketService {

    private final ObjectMapper objectMapper;

    private final HybridEncryptionService hybridEncryptionService;

    public MeshPacketService(
            ObjectMapper objectMapper,
            HybridEncryptionService hybridEncryptionService
    ) {
        this.objectMapper = objectMapper;
        this.hybridEncryptionService =
                hybridEncryptionService;
    }

    public MeshPacket createPacket(
            PaymentInstruction instruction,
            String origin,
            String destination,
            int ttl,
            PublicKey receiverPublicKey,
            PrivateKey senderPrivateKey
    ) throws Exception {

        // 1. Convert payment to JSON
        String paymentJson =
                objectMapper.writeValueAsString(instruction);

        // 2. Sign the payment
        String signature =
                RsaSignatureCrypto.sign(
                        paymentJson,
                        senderPrivateKey
                );

        // 3. Encrypt payment using hybrid encryption
        HybridEncryptedPayload encryptedPayload =
                hybridEncryptionService.encrypt(
                        instruction,
                        receiverPublicKey
                );

        // 4. Create packet
        return new MeshPacket(
                UUID.randomUUID(),
                origin,
                destination,
                ttl,
                Instant.now(),
                encryptedPayload.getEncryptedData(),
                encryptedPayload.getEncryptedKey(),
                signature
        );
    }
}