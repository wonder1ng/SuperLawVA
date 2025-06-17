package first.backtest.storage;

import first.backtest.util.AriaEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
@Primary
@RequiredArgsConstructor
public class EncryptedStorage implements FileStorageService {

    private final LocalFileStorageService delegate;

    /* 저장 ---------------------------------------------------------------- */
    @Override
    public String saveBytes(byte[] plain, String name) throws IOException {
        String b64;
        try {
            b64 = AriaEncryptor.encryptBytes(plain);             // IV+암문 → Base64
        } catch (Exception e) {                                  // <- Encryptor의 checked Exception
            throw new IOException("Encrypt failed", e);          // IOException 래핑
        }
        return delegate.saveBytes(b64.getBytes(StandardCharsets.UTF_8), name);
    }

    /* 읽기 ---------------------------------------------------------------- */
    @Override
    public byte[] loadFile(String path) throws IOException {
        byte[] b64 = delegate.loadFile(path);                    // Base64 암문 읽기
        try {
            return AriaEncryptor.decryptBytes(
                    new String(b64, StandardCharsets.UTF_8));    // 복호화
        } catch (Exception e) {
            throw new IOException("Decrypt failed", e);
        }
    }

    /* 삭제 ---------------------------------------------------------------- */
    @Override
    public void deleteFile(String path) throws IOException {
        delegate.deleteFile(path);
    }
}
