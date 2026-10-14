package com.demo.upi_offline_mesh.service;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PacketDeduplicationServiceTest {

    @Test
    void shouldDetectDuplicatePacket() {

        PacketDeduplicationService service =
                new PacketDeduplicationService();

        UUID packetId =
                UUID.randomUUID();

        assertFalse(
                service.isDuplicate(packetId)
        );

        service.markSeen(packetId);

        assertTrue(
                service.isDuplicate(packetId)
        );
    }


    @Test
    void differentPacketsShouldNotBeDuplicates() {

        PacketDeduplicationService service =
                new PacketDeduplicationService();

        UUID packet1 =
                UUID.randomUUID();

        UUID packet2 =
                UUID.randomUUID();

        service.markSeen(packet1);

        assertFalse(
                service.isDuplicate(packet2)
        );
    }


    @Test
    void shouldDetectDuplicatePayment() {

        PacketDeduplicationService service =
                new PacketDeduplicationService();

        UUID paymentId =
                UUID.randomUUID();

        assertFalse(
                service.isPaymentDuplicate(paymentId)
        );

        service.markPaymentSeen(paymentId);

        assertTrue(
                service.isPaymentDuplicate(paymentId)
        );
    }


    @Test
    void differentPaymentsShouldNotBeDuplicates() {

        PacketDeduplicationService service =
                new PacketDeduplicationService();

        UUID payment1 =
                UUID.randomUUID();

        UUID payment2 =
                UUID.randomUUID();

        service.markPaymentSeen(payment1);

        assertFalse(
                service.isPaymentDuplicate(payment2)
        );
    }


    @Test
    void differentPacketsWithSamePaymentAreStillDuplicatePayment() {

        PacketDeduplicationService service =
                new PacketDeduplicationService();

        UUID packet1 =
                UUID.randomUUID();

        UUID packet2 =
                UUID.randomUUID();

        UUID paymentId =
                UUID.randomUUID();

        // Different packets
        assertNotEquals(
                packet1,
                packet2
        );

        // Same payment
        service.markPaymentSeen(paymentId);

        assertTrue(
                service.isPaymentDuplicate(paymentId)
        );
    }
}