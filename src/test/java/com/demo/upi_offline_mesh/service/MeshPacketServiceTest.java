package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.crypto.HybridEncryptionService;
import com.demo.upi_offline_mesh.crypto.RsaOaepCrypto;
import com.demo.upi_offline_mesh.model.MeshPacket;
import com.demo.upi_offline_mesh.model.PaymentInstruction;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MeshPacketServiceTest {

    @Test
    void shouldCreateEncryptedSignedMeshPacket()
            throws Exception {

        ObjectMapper objectMapper =
                new ObjectMapper();

        HybridEncryptionService hybridEncryptionService =
                new HybridEncryptionService(
                        objectMapper
                );

        MeshPacketService service =
                new MeshPacketService(
                        objectMapper,
                        hybridEncryptionService
                );

        // Sender keys
        KeyPair senderKeys =
                RsaOaepCrypto.generateKeyPair();

        // Receiver keys
        KeyPair receiverKeys =
                RsaOaepCrypto.generateKeyPair();

        PaymentInstruction instruction =
                new PaymentInstruction(
                        UUID.randomUUID(),
                        1L,
                        2L,
                        500,
                        "INR",
                        Instant.now(),
                        UUID.randomUUID().toString()
                );

        MeshPacket packet =
                service.createPacket(
                        instruction,
                        "PHONE-ALICE",
                        "BRIDGE",
                        5,
                        receiverKeys.getPublic(),
                        senderKeys.getPrivate()
                );

        assertNotNull(packet);

        assertNotNull(
                packet.getPacketId()
        );

        assertEquals(
                "PHONE-ALICE",
                packet.getOrigin()
        );

        assertEquals(
                5,
                packet.getTtl()
        );

        assertNotNull(
                packet.getEncryptedData()
        );

        assertNotNull(
                packet.getEncryptedKey()
        );

        assertNotNull(
                packet.getSignature()
        );
    }
}