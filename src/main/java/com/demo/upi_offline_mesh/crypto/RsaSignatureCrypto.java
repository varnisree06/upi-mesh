package com.demo.upi_offline_mesh.crypto;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class RsaSignatureCrypto {

    private static final String ALGORITHM =
            "SHA256withRSA";

    public static String sign(
            String data,
            PrivateKey privateKey
    ) throws Exception {

        Signature signature =
                Signature.getInstance(ALGORITHM);

        signature.initSign(privateKey);

        signature.update(
                data.getBytes(StandardCharsets.UTF_8)
        );

        byte[] signedData =
                signature.sign();

        return Base64.getEncoder()
                .encodeToString(signedData);
    }

    public static boolean verify(
            String data,
            String encodedSignature,
            PublicKey publicKey
    ) throws Exception {

        Signature signature =
                Signature.getInstance(ALGORITHM);

        signature.initVerify(publicKey);

        signature.update(
                data.getBytes(StandardCharsets.UTF_8)
        );

        byte[] signedData =
                Base64.getDecoder()
                        .decode(encodedSignature);

        return signature.verify(signedData);
    }
}