package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.model.FailureReason;
import com.demo.upi_offline_mesh.model.ForwardingResult;
import com.demo.upi_offline_mesh.model.MeshPacket;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeshForwardingService {

    private final MeshNetwork meshNetwork;
    private final MeshRouter meshRouter;
    private final PacketDeliveryService deliveryService;

    public MeshForwardingService(
            MeshNetwork meshNetwork,
            MeshRouter meshRouter,
            PacketDeliveryService deliveryService
    ) {
        this.meshNetwork = meshNetwork;
        this.meshRouter = meshRouter;
        this.deliveryService = deliveryService;
    }

    public ForwardingResult forward(
            String startingNode,
            MeshPacket packet
    ) {

        // -----------------------------------
        // 1. Find shortest route
        // -----------------------------------

        List<String> path =
                meshRouter.findShortestPath(
                        startingNode,
                        packet.getDestination(),
                        meshNetwork
                );

        // No route available
        if (path.isEmpty()) {

            return ForwardingResult.failure(
                    FailureReason.NO_ROUTE
            );
        }


        // -----------------------------------
        // 2. Check TTL
        // -----------------------------------

        if (packet.getTtl() < path.size() - 1) {

            return ForwardingResult.failure(
                    FailureReason.TTL_EXPIRED
            );
        }


        // -----------------------------------
        // 3. Forward packet hop-by-hop
        // -----------------------------------

        for (int i = 0;
             i < path.size() - 1;
             i++) {

            String fromNode =
                    path.get(i);

            String nextHop =
                    path.get(i + 1);


            boolean delivered =
                    deliveryService.deliver(
                            fromNode,
                            nextHop,
                            packet
                    );


            // Delivery failed
            if (!delivered) {

                return ForwardingResult.failure(
                        FailureReason.DELIVERY_FAILED
                );
            }
        }


        // -----------------------------------
        // 4. Successfully reached destination
        // -----------------------------------

        return ForwardingResult.success();
    }
}