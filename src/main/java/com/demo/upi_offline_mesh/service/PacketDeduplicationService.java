package com.demo.upi_offline_mesh.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class PacketDeduplicationService {

    private final Set<UUID> seenPackets =
            new HashSet<>();

    private final Set<UUID> seenPayments =
            new HashSet<>();


    // -------------------------
    // PACKET DUPLICATION
    // -------------------------

    public boolean isDuplicate(UUID packetId) {

        return seenPackets.contains(packetId);
    }

    public void markSeen(UUID packetId) {

        seenPackets.add(packetId);
    }


    // -------------------------
    // PAYMENT REPLAY PROTECTION
    // -------------------------

    public boolean isPaymentDuplicate(
            UUID paymentId
    ) {

        return seenPayments.contains(paymentId);
    }

    public void markPaymentSeen(
            UUID paymentId
    ) {

        seenPayments.add(paymentId);
    }
}