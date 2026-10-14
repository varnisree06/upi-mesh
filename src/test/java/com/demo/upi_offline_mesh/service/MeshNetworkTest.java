package com.demo.upi_offline_mesh.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeshNetworkTest {

    @Test
    void shouldConnectTwoNodes() {

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

        network.connect(
                "A",
                "B"
        );

        assertTrue(
                network.getNeighbors("A")
                        .contains("B")
        );

        assertTrue(
                network.getNeighbors("B")
                        .contains("A")
        );
    }


    @Test
    void shouldReturnNeighborsOfNode() {

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
                "C",
                createNode()
        );

        network.connect(
                "A",
                "B"
        );

        network.connect(
                "A",
                "C"
        );

        assertEquals(
                2,
                network.getNeighbors("A").size()
        );

        assertTrue(
                network.getNeighbors("A")
                        .contains("B")
        );

        assertTrue(
                network.getNeighbors("A")
                        .contains("C")
        );
    }


    @Test
    void shouldDisconnectTwoNodes() {

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

        network.connect(
                "A",
                "B"
        );

        // Connection exists before disconnecting.
        assertTrue(
                network.getNeighbors("A")
                        .contains("B")
        );

        assertTrue(
                network.getNeighbors("B")
                        .contains("A")
        );


        // Remove the connection.
        network.disconnect(
                "A",
                "B"
        );


        // Connection should no longer exist.
        assertFalse(
                network.getNeighbors("A")
                        .contains("B")
        );

        assertFalse(
                network.getNeighbors("B")
                        .contains("A")
        );
    }


    @Test
    void shouldCreateNetworkPartition() {

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
         * A → B → C → BRIDGE
         *
         * There is a route from A to BRIDGE.
         */

        network.connect(
                "A",
                "B"
        );

        network.connect(
                "B",
                "C"
        );

        network.connect(
                "C",
                "BRIDGE"
        );


        assertFalse(
                new MeshRouter()
                        .findShortestPath(
                                "A",
                                "BRIDGE",
                                network
                        )
                        .isEmpty()
        );


        /*
         * Create a network partition:
         *
         * A → B       C → BRIDGE
         *
         * B and C can no longer communicate.
         */

        network.disconnect(
                "B",
                "C"
        );


        /*
         * A can no longer reach BRIDGE.
         */
        assertTrue(
                new MeshRouter()
                        .findShortestPath(
                                "A",
                                "BRIDGE",
                                network
                        )
                        .isEmpty()
        );
    }


    @Test
    void shouldRecoverAfterNetworkReconnects() {

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
         * A → B → C → BRIDGE
         */

        network.connect(
                "A",
                "B"
        );

        network.connect(
                "B",
                "C"
        );

        network.connect(
                "C",
                "BRIDGE"
        );


        /*
         * Create partition:
         *
         * A → B       C → BRIDGE
         */

        network.disconnect(
                "B",
                "C"
        );


        assertTrue(
                new MeshRouter()
                        .findShortestPath(
                                "A",
                                "BRIDGE",
                                network
                        )
                        .isEmpty()
        );


        /*
         * Network reconnects:
         *
         * A → B → C → BRIDGE
         */

        network.connect(
                "B",
                "C"
        );


        /*
         * Route should be available again.
         */

        assertFalse(
                new MeshRouter()
                        .findShortestPath(
                                "A",
                                "BRIDGE",
                                network
                        )
                        .isEmpty()
        );
    }


    private MeshNode createNode() {

        return new MeshNode(
                new MeshRouter(),
                new PacketDeduplicationService()
        );
    }
}