package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.MeshPacket;
import org.springframework.stereotype.Service;

@Service
public class MeshSimulationService {

    private final PacketDeliveryService deliveryService;

    public MeshSimulationService(
            PacketDeliveryService deliveryService
    ) {
        this.deliveryService =
                deliveryService;
    }

    public boolean send(
            String fromNode,
            String toNode,
            MeshPacket packet
    ) {

        return deliveryService.deliver(
                fromNode,
                toNode,
                packet
        );
    }
}