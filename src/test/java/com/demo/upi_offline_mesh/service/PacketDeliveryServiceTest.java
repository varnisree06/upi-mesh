package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.MeshPacket;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PacketDeliveryServiceTest {

    @Test
    void shouldDeliverPacketToConnectedNode() {

        MeshNetwork network =
                new MeshNetwork();

        MeshNode nodeA =
                createNode();

        MeshNode nodeB =
                createNode();

        network.addNode("A", nodeA);
        network.addNode("B", nodeB);

        network.connect("A", "B");

        PacketDeliveryService deliveryService =
                new PacketDeliveryService(network);

        MeshPacket packet =
                createPacket(3);

        boolean delivered =
                deliveryService.deliver(
                        "A",
                        "B",
                        packet
                );

        assertTrue(delivered);

        assertEquals(
                2,
                packet.getTtl()
        );
    }

    @Test
    void shouldRejectDeliveryWhenNodesAreNotConnected() {

        MeshNetwork network =
                new MeshNetwork();

        network.addNode(
                "A",
                createNode()
        );

        network.addNode(
                "B",
                createNode()
        );

        PacketDeliveryService deliveryService =
                new PacketDeliveryService(network);

        MeshPacket packet =
                createPacket(3);

        boolean delivered =
                deliveryService.deliver(
                        "A",
                        "B",
                        packet
                );

        assertFalse(delivered);
    }

    private MeshNode createNode() {

        return new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        );
    }

    private MeshPacket createPacket(
            int ttl
    ) {

        return new MeshPacket(
                UUID.randomUUID(),
                "A",
                "B",
                ttl,
                Instant.now(),
                "encrypted-data",
                "encrypted-key",
                "signature"
        );
    }
}