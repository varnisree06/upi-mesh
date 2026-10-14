package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.FailureReason;
import com.demo.upi_offline_mesh.model.ForwardingResult;
import com.demo.upi_offline_mesh.model.MeshPacket;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MeshForwardingServiceTest {

    @Test
    void shouldForwardPacketAcrossMultipleNodes() {

        MeshNetwork network =
                new MeshNetwork();

        MeshNode nodeA = createNode();
        MeshNode nodeB = createNode();
        MeshNode nodeC = createNode();
        MeshNode bridge = createNode();

        network.addNode("A", nodeA);
        network.addNode("B", nodeB);
        network.addNode("C", nodeC);
        network.addNode("D", createNode());
        network.addNode("BRIDGE", bridge);

        // Shortest route:
        //
        // A → B → BRIDGE
        //
        network.connect("A", "B");
        network.connect("B", "BRIDGE");

        // Longer alternative route:
        //
        // A → C → D → BRIDGE
        //
        network.connect("A", "C");
        network.connect("C", "D");
        network.connect("D", "BRIDGE");


        MeshRouter router =
                new MeshRouter();

        PacketDeliveryService deliveryService =
                new PacketDeliveryService(network);

        MeshForwardingService forwardingService =
                new MeshForwardingService(
                        network,
                        router,
                        deliveryService
                );


        MeshPacket packet =
                new MeshPacket(
                        UUID.randomUUID(),
                        "A",
                        "BRIDGE",
                        5,
                        Instant.now(),
                        "encrypted-data",
                        "encrypted-key",
                        "signature"
                );


        ForwardingResult result =
                forwardingService.forward(
                        "A",
                        packet
                );


        assertTrue(
                result.isSuccess()
        );

        assertNull(
                result.getFailureReason()
        );


        // Shortest path has 2 hops:
        //
        // 5 → 4 → 3
        //
        assertEquals(
                3,
                packet.getTtl()
        );
    }


    @Test
    void shouldFailWhenNoRouteExists() {

        MeshNetwork network =
                new MeshNetwork();

        MeshNode nodeA = createNode();
        MeshNode nodeB = createNode();
        MeshNode bridge = createNode();

        network.addNode("A", nodeA);
        network.addNode("B", nodeB);
        network.addNode("BRIDGE", bridge);


        // A → B
        network.connect(
                "A",
                "B"
        );

        // BRIDGE is isolated.
        //
        // A cannot reach BRIDGE.


        MeshRouter router =
                new MeshRouter();

        PacketDeliveryService deliveryService =
                new PacketDeliveryService(network);

        MeshForwardingService forwardingService =
                new MeshForwardingService(
                        network,
                        router,
                        deliveryService
                );


        MeshPacket packet =
                new MeshPacket(
                        UUID.randomUUID(),
                        "A",
                        "BRIDGE",
                        5,
                        Instant.now(),
                        "encrypted-data",
                        "encrypted-key",
                        "signature"
                );


        ForwardingResult result =
                forwardingService.forward(
                        "A",
                        packet
                );


        assertFalse(
                result.isSuccess()
        );

        assertEquals(
                FailureReason.NO_ROUTE,
                result.getFailureReason()
        );
    }


    @Test
    void shouldFailWhenTtlIsTooSmall() {

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

        network.addNode(
                "BRIDGE",
                createNode()
        );


        network.connect(
                "A",
                "B"
        );

        network.connect(
                "B",
                "BRIDGE"
        );


        MeshRouter router =
                new MeshRouter();

        PacketDeliveryService deliveryService =
                new PacketDeliveryService(network);

        MeshForwardingService forwardingService =
                new MeshForwardingService(
                        network,
                        router,
                        deliveryService
                );


        // Route requires 2 hops.
        //
        // But TTL = 1.
        //
        MeshPacket packet =
                new MeshPacket(
                        UUID.randomUUID(),
                        "A",
                        "BRIDGE",
                        1,
                        Instant.now(),
                        "encrypted-data",
                        "encrypted-key",
                        "signature"
                );


        ForwardingResult result =
                forwardingService.forward(
                        "A",
                        packet
                );


        assertFalse(
                result.isSuccess()
        );

        assertEquals(
                FailureReason.TTL_EXPIRED,
                result.getFailureReason()
        );
    }


    private MeshNode createNode() {

        return new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        );
    }

    @Test
    void shouldFailWhenPacketDeliveryFails() {

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

        network.addNode(
                "BRIDGE",
                createNode()
        );


        network.connect(
                "A",
                "B"
        );

        network.connect(
                "B",
                "BRIDGE"
        );


        MeshRouter router =
                new MeshRouter();


        PacketDeliveryService deliveryService =
                mock(PacketDeliveryService.class);


        when(
                deliveryService.deliver(
                        anyString(),
                        anyString(),
                        any(MeshPacket.class)
                )
        ).thenReturn(false);


        MeshForwardingService forwardingService =
                new MeshForwardingService(
                        network,
                        router,
                        deliveryService
                );


        MeshPacket packet =
                new MeshPacket(
                        UUID.randomUUID(),
                        "A",
                        "BRIDGE",
                        5,
                        Instant.now(),
                        "encrypted-data",
                        "encrypted-key",
                        "signature"
                );


        ForwardingResult result =
                forwardingService.forward(
                        "A",
                        packet
                );


        assertFalse(
                result.isSuccess()
        );


        assertEquals(
                FailureReason.DELIVERY_FAILED,
                result.getFailureReason()
        );
    }
}