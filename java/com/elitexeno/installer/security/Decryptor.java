package com.elitexeno.installer.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Arrays;

public class Decryptor {

    private static final byte[] FORMAT_MAGIC = new byte[]{'E', 'X', 'N', 'O', 'B', 'I', 'N', '1'};
    private static final int SALT_LEN = 16;
    private static final int IV_LEN = 16;
    private static final int HMAC_LEN = 32;
    private static final int PBKDF2_ITERATIONS = 120000;
    private static final int DERIVED_LEN = 64;

    public static File decrypt(android.content.Context context, File inputFile) throws Exception {
        byte[] input = readAllBytes(inputFile);

        if (isApkZip(input)) {
            return inputFile;
        }

        if (hasFormatHeader(input)) {
            File outFile = new File(context.getCacheDir(), "dec.apk");
            decryptExnoBin(input, outFile);
            return outFile;
        }

        if (hasOpenSslSaltHeader(input)) {
            File outFile = new File(context.getCacheDir(), "dec.apk");
            decryptOpenSsl(input, outFile);
            return outFile;
        }

        throw new IllegalStateException("Unsupported payload format");
    }

    private static void decryptExnoBin(byte[] input, File outFile) throws Exception {
        int minSize = FORMAT_MAGIC.length + SALT_LEN + IV_LEN + 4 + HMAC_LEN;
        if (input.length < minSize) {
            throw new IllegalStateException("Invalid payload");
        }

        int offset = FORMAT_MAGIC.length;
        byte[] salt = Arrays.copyOfRange(input, offset, offset + SALT_LEN);
        offset += SALT_LEN;

        byte[] iv = Arrays.copyOfRange(input, offset, offset + IV_LEN);
        offset += IV_LEN;

        int cipherLen = ByteBuffer.wrap(input, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
        offset += 4;

        if (cipherLen <= 0 || offset + cipherLen + HMAC_LEN != input.length) {
            throw new IllegalStateException("Invalid payload length");
        }

        byte[] cipherText = Arrays.copyOfRange(input, offset, offset + cipherLen);
        byte[] providedHmac = Arrays.copyOfRange(input, offset + cipherLen, input.length);

        byte[] derived = deriveKeyMaterial(KeyManager.getKey(), salt, DERIVED_LEN);
        byte[] encKey = Arrays.copyOfRange(derived, 0, 32);
        byte[] macKey = Arrays.copyOfRange(derived, 32, 64);

        byte[] signedPortion = Arrays.copyOfRange(input, 0, input.length - HMAC_LEN);
        byte[] computedHmac = hmacSha256(macKey, signedPortion);
        if (!MessageDigest.isEqual(providedHmac, computedHmac)) {
            throw new IllegalStateException("Payload integrity check failed");
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encKey, "AES"), new IvParameterSpec(iv));
        byte[] plain = cipher.doFinal(cipherText);

        if (!isApkZip(plain)) {
            throw new IllegalStateException("Decrypted payload is not a valid APK");
        }

        FileOutputStream out = new FileOutputStream(outFile);
        out.write(plain);
        out.close();
    }

    private static void decryptOpenSsl(byte[] input, File outFile) throws Exception {
        byte[] salt = Arrays.copyOfRange(input, 8, 16);
        byte[] cipherText = Arrays.copyOfRange(input, 16, input.length);
        String passphrase = KeyManager.getKey();

        byte[] plain = tryLegacyOpenSsl(passphrase, salt, cipherText, 32, "MD5");
        if (plain == null) {
            plain = tryLegacyOpenSsl(passphrase, salt, cipherText, 24, "MD5");
        }
        if (plain == null) {
            plain = tryLegacyOpenSsl(passphrase, salt, cipherText, 16, "MD5");
        }
        if (plain == null) {
            plain = tryLegacyOpenSsl(passphrase, salt, cipherText, 32, "SHA-256");
        }
        if (plain == null) {
            plain = tryLegacyOpenSsl(passphrase, salt, cipherText, 24, "SHA-256");
        }
        if (plain == null) {
            plain = tryLegacyOpenSsl(passphrase, salt, cipherText, 16, "SHA-256");
        }
        if (plain == null) {
            plain = tryLegacyOpenSsl(passphrase, salt, cipherText, 32, "SHA-1");
        }
        if (plain == null) {
            plain = tryPbkdf2OpenSsl(passphrase, salt, cipherText, 32);
        }
        if (plain == null) {
            plain = tryPbkdf2OpenSsl(passphrase, salt, cipherText, 16);
        }

        if (plain == null || !isApkZip(plain)) {
            throw new IllegalStateException("Unable to decrypt payload with configured key");
        }

        FileOutputStream out = new FileOutputStream(outFile);
        out.write(plain);
        out.close();
    }

    private static byte[] tryLegacyOpenSsl(String passphrase, byte[] salt, byte[] cipherText, int keyLen, String digest) {
        try {
            byte[] passphraseBytes = passphrase.getBytes("UTF-8");
            byte[][] keyIv = evpBytesToKey(passphraseBytes, salt, keyLen, 16, digest);

            SecretKeySpec keySpec = new SecretKeySpec(keyIv[0], "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyIv[1]);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(cipherText);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static byte[] tryPbkdf2OpenSsl(String passphrase, byte[] salt, byte[] cipherText, int keyLen) {
        try {
            PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, 10000, (keyLen + 16) * 8);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] derived = secretKeyFactory.generateSecret(spec).getEncoded();

            byte[] key = Arrays.copyOfRange(derived, 0, keyLen);
            byte[] iv = Arrays.copyOfRange(derived, keyLen, keyLen + 16);

            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(cipherText);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static byte[][] evpBytesToKey(byte[] passphrase, byte[] salt, int keyLen, int ivLen, String digest) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(digest);
        byte[] result = new byte[0];
        byte[] prev = new byte[0];

        while (result.length < keyLen + ivLen) {
            messageDigest.reset();
            messageDigest.update(prev);
            messageDigest.update(passphrase);
            messageDigest.update(salt);
            prev = messageDigest.digest();
            result = concat(result, prev);
        }

        byte[] key = Arrays.copyOfRange(result, 0, keyLen);
        byte[] iv = Arrays.copyOfRange(result, keyLen, keyLen + ivLen);
        return new byte[][]{key, iv};
    }

    private static boolean hasOpenSslSaltHeader(byte[] input) {
        if (input.length < 16) {
            return false;
        }
        return input[0] == 'S' && input[1] == 'a' && input[2] == 'l' && input[3] == 't'
                && input[4] == 'e' && input[5] == 'd' && input[6] == '_' && input[7] == '_';
    }

    private static boolean hasFormatHeader(byte[] input) {
        if (input.length < FORMAT_MAGIC.length) {
            return false;
        }
        for (int i = 0; i < FORMAT_MAGIC.length; i++) {
            if (input[i] != FORMAT_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isApkZip(byte[] input) {
        return input.length >= 4
                && input[0] == 'P' && input[1] == 'K'
                && input[2] == 0x03 && input[3] == 0x04;
    }

    private static byte[] deriveKeyMaterial(String passphrase, byte[] salt, int bytes) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERATIONS, bytes * 8);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return secretKeyFactory.generateSecret(spec).getEncoded();
    }

    private static byte[] hmacSha256(byte[] key, byte[] content) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(content);
    }

    private static byte[] readAllBytes(File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;

        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

        in.close();
        return out.toByteArray();
    }

    private static byte[] concat(byte[] first, byte[] second) {
        byte[] merged = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, merged, first.length, second.length);
        return merged;
    }
}