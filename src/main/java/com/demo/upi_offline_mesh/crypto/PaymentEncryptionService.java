package com.demo.upi_offline_mesh.crypto;

import com.demo.upi_offline_mesh.model.PaymentInstruction;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PaymentEncryptionService {

    private static final String ALGORITHM =
            "AES/GCM/NoPadding";

    private static final int GCM_TAG_LENGTH =
            128;

    private static final int IV_LENGTH =
            12;

    private final ObjectMapper objectMapper;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public PaymentEncryptionService(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    public String encryptPayment(
            PaymentInstruction payment,
            SecretKey key
    ) throws Exception {

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Payment cannot be null"
            );
        }

        if (key == null) {
            throw new IllegalArgumentException(
                    "Encryption key cannot be null"
            );
        }

        /*
         * Convert payment into JSON.
         */
        String json =
                objectMapper.writeValueAsString(
                        payment
                );

        /*
         * Generate a fresh IV for every encryption.
         */
        byte[] iv =
                new byte[IV_LENGTH];

        secureRandom.nextBytes(iv);

        /*
         * AES-GCM encryption.
         */
        Cipher cipher =
                Cipher.getInstance(ALGORITHM);

        GCMParameterSpec gcmSpec =
                new GCMParameterSpec(
                        GCM_TAG_LENGTH,
                        iv
                );

        cipher.init(
                Cipher.ENCRYPT_MODE,
                key,
                gcmSpec
        );

        byte[] encrypted =
                cipher.doFinal(
                        json.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        /*
         * Store IV + ciphertext together.
         *
         * The receiver needs the IV to decrypt.
         */
        byte[] result =
                new byte[
                        iv.length + encrypted.length
                        ];

        System.arraycopy(
                iv,
                0,
                result,
                0,
                iv.length
        );

        System.arraycopy(
                encrypted,
                0,
                result,
                iv.length,
                encrypted.length
        );

        return Base64.getEncoder()
                .encodeToString(result);
    }
}