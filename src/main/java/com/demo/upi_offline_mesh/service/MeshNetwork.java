package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.MeshPacket;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MeshNetwork {

    private final Map<String, MeshNode> nodes =
            new HashMap<>();

    private final Map<String, Set<String>> connections =
            new HashMap<>();

    public void addNode(
            String nodeId,
            MeshNode node
    ) {
        nodes.put(nodeId, node);
        connections.putIfAbsent(
                nodeId,
                new HashSet<>()
        );
    }

    public void connect(
            String nodeA,
            String nodeB
    ) {

        connections
                .get(nodeA)
                .add(nodeB);

        connections
                .get(nodeB)
                .add(nodeA);
    }

    public Set<String> getNeighbors(
            String nodeId
    ) {

        return connections.getOrDefault(
                nodeId,
                Collections.emptySet()
        );
    }

    public MeshNode getNode(
            String nodeId
    ) {

        return nodes.get(nodeId);
    }

    public void disconnect(
            String nodeA,
            String nodeB
    ) {

        Set<String> neighborsOfA =
                connections.get(nodeA);

        Set<String> neighborsOfB =
                connections.get(nodeB);

        if (neighborsOfA != null) {
            neighborsOfA.remove(nodeB);
        }

        if (neighborsOfB != null) {
            neighborsOfB.remove(nodeA);
        }
    }
}