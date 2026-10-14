package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.MeshPacket;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MeshRouter {

    public boolean canForward(MeshPacket packet) {

        return packet.getTtl() > 0;
    }

    public void decrementTtl(MeshPacket packet) {

        if (packet.getTtl() > 0) {
            packet.setTtl(
                    packet.getTtl() - 1
            );
        }
    }

    public String chooseNextHop(
            String currentNode,
            String fromNode,
            String destination,
            MeshNetwork network
    ) {

        var neighbors =
                network.getNeighbors(currentNode);

        // Directly connected to destination
        if (neighbors.contains(destination)) {
            return destination;
        }

        // Pick another neighbor, avoiding sender
        return neighbors.stream()
                .filter(node -> !node.equals(fromNode))
                .findFirst()
                .orElse(null);
    }

    public List<String> findShortestPath(
            String start,
            String destination,
            MeshNetwork network
    ) {

        Queue<String> queue =
                new LinkedList<>();

        Map<String, String> previous =
                new HashMap<>();

        Set<String> visited =
                new HashSet<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {

            String current =
                    queue.poll();

            if (current.equals(destination)) {
                break;
            }

            for (String neighbor :
                    network.getNeighbors(current)) {

                if (!visited.contains(neighbor)) {

                    visited.add(neighbor);

                    previous.put(
                            neighbor,
                            current
                    );

                    queue.offer(neighbor);
                }
            }
        }

        if (!visited.contains(destination)) {
            return List.of();
        }

        List<String> path =
                new ArrayList<>();

        String current = destination;

        while (current != null) {

            path.add(current);

            current =
                    previous.get(current);
        }

        Collections.reverse(path);

        return path;
    }
}