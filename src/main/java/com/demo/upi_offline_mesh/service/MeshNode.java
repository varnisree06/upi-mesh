package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.MeshPacket;
import org.springframework.stereotype.Component;

@Component
public class MeshNode {

    private final MeshRouter meshRouter;
    private final PacketDeduplicationService deduplicationService;

    public MeshNode(
            MeshRouter meshRouter,
            PacketDeduplicationService deduplicationService
    ) {
        this.meshRouter = meshRouter;
        this.deduplicationService =
                deduplicationService;
    }

    public boolean receivePacket(
            String fromNode,
            MeshPacket packet
    ) {

        if (deduplicationService.isDuplicate(
                packet.getPacketId()
        )) {
            return false;
        }

        deduplicationService.markSeen(
                packet.getPacketId()
        );

        if (!meshRouter.canForward(packet)) {
            return false;
        }

        meshRouter.decrementTtl(packet);

        return true;
    }
}