package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.MeshPacket;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MeshNodeTest {

    @Test
    void shouldAcceptNewPacketAndDecreaseTtl() {

        MeshRouter router =
                new MeshRouter();

        PacketDeduplicationService deduplicationService =
                new PacketDeduplicationService();

        MeshNode node =
                new MeshNode(
                        router,
                        deduplicationService
                );

        MeshPacket packet =
                createPacket(3);

        boolean accepted =
                node.receivePacket(
                        "SOURCE",
                        packet
                );

        assertTrue(accepted);

        assertEquals(
                2,
                packet.getTtl()
        );
    }

    @Test
    void shouldRejectDuplicatePacket() {

        MeshRouter router =
                new MeshRouter();

        PacketDeduplicationService deduplicationService =
                new PacketDeduplicationService();

        MeshNode node =
                new MeshNode(
                        router,
                        deduplicationService
                );

        MeshPacket packet =
                createPacket(3);

        assertTrue(
                node.receivePacket(
                        "SOURCE",
                        packet
                )
        );

        assertFalse(
                node.receivePacket(
                        "SOURCE",
                        packet
                )
        );
    }

    @Test
    void shouldRejectPacketWhenTtlIsZero() {

        MeshRouter router =
                new MeshRouter();

        PacketDeduplicationService deduplicationService =
                new PacketDeduplicationService();

        MeshNode node =
                new MeshNode(
                        router,
                        deduplicationService
                );

        MeshPacket packet =
                createPacket(0);

        assertFalse(
                node.receivePacket(
                        "SOURCE",
                        packet
                )
        );
    }

    private MeshPacket createPacket(int ttl) {

        return new MeshPacket(
                UUID.randomUUID(),
                "PHONE-A",
                "BRIDGE",
                ttl,
                Instant.now(),
                "encrypted-data",
                "encrypted-key",
                "signature"
        );
    }
}