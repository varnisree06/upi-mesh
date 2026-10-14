package com.demo.upi_offline_mesh.service;

import java.util.*;

import com.demo.upi_offline_mesh.model.MeshPacket;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MeshRouterTest {

    @Test
    void shouldAllowForwardingWhenTtlIsPositive() {

        MeshPacket packet =
                new MeshPacket(
                        UUID.randomUUID(),
                        "PHONE-A",
                        "BRIDGE",
                        3,
                        Instant.now(),
                        "encrypted-data",
                        "encrypted-key",
                        "signature"
                );

        MeshRouter router =
                new MeshRouter();

        assertTrue(
                router.canForward(packet)
        );
    }

    @Test
    void shouldNotAllowForwardingWhenTtlIsZero() {

        MeshPacket packet =
                new MeshPacket(
                        UUID.randomUUID(),
                        "PHONE-A",
                        "BRIDGE",
                        0,
                        Instant.now(),
                        "encrypted-data",
                        "encrypted-key",
                        "signature"
                );

        MeshRouter router =
                new MeshRouter();

        assertFalse(
                router.canForward(packet)
        );
    }
    @Test
    void shouldDecreaseTtlByOne() {

        MeshPacket packet =
                new MeshPacket(
                        UUID.randomUUID(),
                        "PHONE-A",
                        "BRIDGE",
                        3,
                        Instant.now(),
                        "encrypted-data",
                        "encrypted-key",
                        "signature"
                );

        MeshRouter router =
                new MeshRouter();

        assertEquals(3, packet.getTtl());

        router.decrementTtl(packet);

        assertEquals(2, packet.getTtl());
    }

    @Test
    void shouldChooseDestinationWhenDirectlyConnected() {

        MeshNetwork network =
                new MeshNetwork();

        network.addNode("A", new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        ));

        network.addNode("B", new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        ));

        network.addNode("C", new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        ));

        network.connect("A", "B");
        network.connect("B", "C");

        MeshRouter router =
                new MeshRouter();

        String nextHop =
                router.chooseNextHop(
                        "B",
                        "A",
                        "C",
                        network
                );

        assertEquals(
                "C",
                nextHop
        );
    }

    @Test
    void shouldFindShortestPath() {

        MeshNetwork network =
                new MeshNetwork();

        network.addNode("A", new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        ));

        network.addNode("B", new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        ));

        network.addNode("C", new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        ));

        network.addNode("D", new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        ));

        network.connect("A", "B");
        network.connect("B", "C");
        network.connect("C", "D");

        MeshRouter router =
                new MeshRouter();

        var path =
                router.findShortestPath(
                        "A",
                        "D",
                        network
                );

        assertEquals(
                List.of("A", "B", "C", "D"),
                path
        );
    }
}