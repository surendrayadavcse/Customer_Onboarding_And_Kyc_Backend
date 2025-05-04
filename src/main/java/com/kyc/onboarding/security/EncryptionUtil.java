package com.kyc.onboarding.security;

import com.kyc.onboarding.exception.EncryptionException;

import javax.crypto.Cipher;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY = System.getenv("ENCRYPTION_KEY"); // Fetch key from environment variable
    private EncryptionUtil() {
        throw new UnsupportedOperationException("EncryptionUtil is a utility class and cannot be instantiated.");
    }
    // Ensure the key length is 16 bytes (128-bit AES)
    private static final int GCM_TAG_LENGTH = 16; // GCM tag length in bytes

    public static String encrypt(String data) {
        try {
            if (KEY == null || KEY.length() != 16) {
                throw new EncryptionException("Invalid encryption key.", null);
            }

            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);

            // Generate a random 12-byte IV for AES/GCM (recommended size for GCM)
            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv); // GCM tag length in bits

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

            byte[] encrypted = cipher.doFinal(data.getBytes());
            // Combine IV + encrypted data + tag
            byte[] encryptedDataWithIvAndTag = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encryptedDataWithIvAndTag, 0, iv.length);
            System.arraycopy(encrypted, 0, encryptedDataWithIvAndTag, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(encryptedDataWithIvAndTag);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt data", e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            if (KEY == null || KEY.length() != 16) {
                throw new EncryptionException("Invalid encryption key.", null);
            }

            // Decode the encrypted data and extract the IV
            byte[] encryptedDataWithIvAndTag = Base64.getDecoder().decode(encryptedData);
            byte[] iv = new byte[12]; // AES/GCM uses a 12-byte IV
            byte[] encrypted = new byte[encryptedDataWithIvAndTag.length - iv.length];

            System.arraycopy(encryptedDataWithIvAndTag, 0, iv, 0, iv.length);
            System.arraycopy(encryptedDataWithIvAndTag, iv.length, encrypted, 0, encrypted.length);

            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            throw new EncryptionException("Failed to decrypt data", e);
        }
    }
}
