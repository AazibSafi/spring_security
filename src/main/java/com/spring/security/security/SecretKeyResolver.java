package com.spring.security.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/*
 *  Aazib Safi
 */
@Component
public class SecretKeyResolver {

    private static String algorithm = "RSA";

    @Value("${app.rsaPrivateKey}")
    private String rsaPrivateKey;

    @Value("${app.rsaPublicKey}")
    private String rsaPublicKey;

    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        rsaPrivateKey = refineKey(rsaPrivateKey, "PRIVATE");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(rsaPrivateKey));
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(keySpec);
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        rsaPublicKey = refineKey(rsaPublicKey, "PUBLIC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(keySpec);
    }

    private String refineKey(String key, String type) {
        key = key.replace("-----BEGIN " + type + " KEY-----", "");
        key = key.replace("-----END " + type + " KEY-----", "");
        key = key.replace("-----BEGIN RSA " + type + " KEY-----", "");
        key = key.replace("-----END RSA " + type + " KEY-----", "");
        key = key.replace(" ", "");
        return key;
    }

}
