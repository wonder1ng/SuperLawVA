package first.backtest.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AriaEncryptor {

        // 256비트 키 (32바이트)
        private static final String SECRET_KEY = "0123456789abcdef0123456789abcdef";
        private static final String ALGORITHM = "ARIA/CBC/PKCS5Padding";
        private static final int IV_SIZE = 16;

        private static SecretKey getSecretKey() {
            return new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "ARIA");
        }

        // String -> 암호문 (Base64)
        public static String encrypt(String plainText) throws Exception {
            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = encryptInternal(plainBytes);
            return Base64.getEncoder().encodeToString(encrypted);
        }

        // 암호문 (Base64) -> String
        public static String decrypt(String base64CipherText) throws Exception {
            byte[] encryptedBytes = Base64.getDecoder().decode(base64CipherText);
            byte[] decrypted = decryptInternal(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        }

        // byte[] 입력 -> 암호화된 byte[] 출력(Base64)
        public static String encryptBytes(byte[] data) throws Exception {
            byte[] encrypted = encryptInternal(data);
            return Base64.getEncoder().encodeToString(encrypted);
        }

        // byte[] 복호화 (Base64 인코딩된 암호문 입력)
        public static byte[] decryptBytes(String base64Encrypted) throws Exception {
            byte[] encryptedBytes = Base64.getDecoder().decode(base64Encrypted);
            return decryptInternal(encryptedBytes);
        }

        // 내부 암호화 로직 (IV 앞에 붙여서 리턴)
        private static byte[] encryptInternal(byte[] plainBytes) throws Exception {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKey key = getSecretKey();

            byte[] iv = new byte[IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(plainBytes);

            // IV + 암호문을 붙여서 리턴
            byte[] encryptedWithIv = new byte[IV_SIZE + encrypted.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, IV_SIZE);
            System.arraycopy(encrypted, 0, encryptedWithIv, IV_SIZE, encrypted.length);
            return encryptedWithIv;
        }

        // 내부 복호화 로직
        private static byte[] decryptInternal(byte[] encryptedWithIv) throws Exception {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKey key = getSecretKey();

            byte[] iv = new byte[IV_SIZE];
            byte[] encrypted = new byte[encryptedWithIv.length - IV_SIZE];

            System.arraycopy(encryptedWithIv, 0, iv, 0, IV_SIZE);
            System.arraycopy(encryptedWithIv, IV_SIZE, encrypted, 0, encrypted.length);

            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher.doFinal(encrypted);
        }
    }

