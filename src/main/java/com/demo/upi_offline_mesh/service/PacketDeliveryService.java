package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.MeshPacket;
import org.springframework.stereotype.Service;

@Service
public class PacketDeliveryService {

    private final MeshNetwork meshNetwork;

    public PacketDeliveryService(
            MeshNetwork meshNetwork
    ) {
        this.meshNetwork = meshNetwork;
    }

    public boolean deliver(
            String fromNode,
            String toNode,
            MeshPacket packet
    ) {

        if (!meshNetwork
                .getNeighbors(fromNode)
                .contains(toNode)) {

            return false;
        }

        MeshNode destination =
                meshNetwork.getNode(toNode);

        if (destination == null) {
            return false;
        }

        return destination.receivePacket(
                fromNode,
                packet
        );
    }
}