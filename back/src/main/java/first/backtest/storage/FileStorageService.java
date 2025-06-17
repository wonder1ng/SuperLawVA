package first.backtest.storage;

import java.io.IOException;

/** 실제 I/O 계층 추상화 – 서비스·컨트롤러는 이 인터페이스만 의존 */
public interface FileStorageService {

    /** (이미 암호화된) 바이트를 저장하고 “내부 경로”를 반환 */
    String saveBytes(byte[] data, String originalName) throws IOException;

    /** 내부 경로 → 암호문 바이트 로드 */
    byte[] loadFile(String path) throws IOException;

    /** 내부 경로 삭제 */
    void deleteFile(String path) throws IOException;
}
