package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.MeshPacket;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RetryRecoveryIntegrationTest {

    @Test
    void shouldRecoverAfterNetworkPartitionIsRestored() {

        MeshNetwork network =
                new MeshNetwork();

        /*
         * Create nodes.
         */

        network.addNode(
                "A",
                createNode()
        );

        network.addNode(
                "B",
                createNode()
        );

        network.addNode(
                "C",
                createNode()
        );

        network.addNode(
                "BRIDGE",
                createNode()
        );


        /*
         * Initial network:
         *
         * A ---- B
         *
         * C ---- BRIDGE
         *
         * B and C are disconnected.
         */

        network.connect(
                "A",
                "B"
        );

        network.connect(
                "C",
                "BRIDGE"
        );


        MeshRouter router =
                new MeshRouter();

        PacketDeliveryService deliveryService =
                new PacketDeliveryService(
                        network
                );

        MeshForwardingService forwardingService =
                new MeshForwardingService(
                        network,
                        router,
                        deliveryService
                );

        RetryDecisionService retryDecisionService =
                new RetryDecisionService();

        RetryService retryService =
                new RetryService(
                        forwardingService,
                        retryDecisionService
                );


        /*
         * Create payment packet.
         */

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


        /*
         * At this moment there is no route:
         *
         * A → B
         *
         * C → BRIDGE
         *
         * Therefore forwarding should fail.
         */

        boolean firstAttempt =
                forwardingService.forward(
                        "A",
                        packet
                ).isSuccess();

        assertFalse(
                firstAttempt
        );


        /*
         * Network is restored.
         *
         * New topology:
         *
         * A → B → C → BRIDGE
         */

        network.connect(
                "B",
                "C"
        );


        /*
         * Retry the payment.
         */

        var result =
                retryService.forwardWithRetry(
                        "A",
                        packet,
                        3
                );


        /*
         * The retry should succeed.
         */

        assertTrue(
                result.isSuccess()
        );
    }


    @Test
    void shouldNotRecoverWhenNetworkRemainsPartitioned() {

        MeshNetwork network =
                new MeshNetwork();

        /*
         * A → B
         *
         * C → BRIDGE
         *
         * No connection between B and C.
         */

        network.addNode(
                "A",
                createNode()
        );

        network.addNode(
                "B",
                createNode()
        );

        network.addNode(
                "C",
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
                "C",
                "BRIDGE"
        );


        MeshRouter router =
                new MeshRouter();

        PacketDeliveryService deliveryService =
                new PacketDeliveryService(
                        network
                );

        MeshForwardingService forwardingService =
                new MeshForwardingService(
                        network,
                        router,
                        deliveryService
                );

        RetryDecisionService retryDecisionService =
                new RetryDecisionService();

        RetryService retryService =
                new RetryService(
                        forwardingService,
                        retryDecisionService
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


        /*
         * Retry while the network remains partitioned.
         */

        var result =
                retryService.forwardWithRetry(
                        "A",
                        packet,
                        3
                );


        /*
         * It should eventually stop.
         */

        assertFalse(
                result.isSuccess()
        );
    }


    private MeshNode createNode() {

        return new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        );
    }
}