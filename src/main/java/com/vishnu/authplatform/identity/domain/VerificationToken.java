package com.vishnu.authplatform.identity.domain;

import java.util.Base64;
import java.util.UUID;

public record VerificationToken(UUID tokenId, String secret) {

    private static final String DELIMITER = ".";

    public VerificationToken {
        if (tokenId == null) {
            throw new IllegalArgumentException("tokenId is required");
        }
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("secret is required");
        }
    }

    public static VerificationToken parse(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            throw new IllegalArgumentException("token is required");
        }

        int delimiterIndex = encoded.indexOf(DELIMITER);
        if (delimiterIndex <= 0 || delimiterIndex >= encoded.length() - 1) {
            throw new IllegalArgumentException("invalid token format");
        }

        try {
            String idPart = encoded.substring(0, delimiterIndex);
            String secretPart = encoded.substring(delimiterIndex + 1);

            byte[] idBytes = Base64.getUrlDecoder().decode(idPart);
            if (idBytes.length != 16) {
                throw new IllegalArgumentException("invalid token format");
            }

            long msb = 0, lsb = 0;
            for (int i = 0; i < 8; i++) {
                msb = (msb << 8) | (idBytes[i] & 0xff);
            }
            for (int i = 8; i < 16; i++) {
                lsb = (lsb << 8) | (idBytes[i] & 0xff);
            }
            UUID tokenId = new UUID(msb, lsb);

            String secret = new String(Base64.getUrlDecoder().decode(secretPart));

            return new VerificationToken(tokenId, secret);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid token format");
        }
    }

    public String encode() {
        byte[] idBytes = new byte[16];
        long msb = tokenId.getMostSignificantBits();
        long lsb = tokenId.getLeastSignificantBits();
        for (int i = 7; i >= 0; i--) {
            idBytes[i] = (byte) (msb & 0xff);
            msb >>= 8;
        }
        for (int i = 15; i >= 8; i--) {
            idBytes[i] = (byte) (lsb & 0xff);
            lsb >>= 8;
        }

        String idEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(idBytes);
        String secretEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(secret.getBytes());

        return idEncoded + DELIMITER + secretEncoded;
    }
}
