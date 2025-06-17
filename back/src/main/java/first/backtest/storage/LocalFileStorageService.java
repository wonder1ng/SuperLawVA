package first.backtest.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

/** 로컬 디스크에 그대로 기록하는 I/O 구현 */
@Slf4j
@Service
@Profile("local")          // prod 프로필엔 S3 구현을 따로 둘 예정
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;         // application.yml 설정

    @Override
    public String saveBytes(byte[] cipher, String originalName) throws IOException {
        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir); // 폴더가 없으면 생성

        String name = System.currentTimeMillis() + "_" + originalName;
        Path path  = dir.resolve(name);

        Files.write(path, cipher);    // 암호문 그대로 기록
        log.info("[로컬저장] 저장 완료: {}", path);
        return path.toString();       // 이 경로는 DB에만 저장, 클라엔 안 보여줌
    }

    @Override
    public byte[] loadFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));    // 암호문 읽기
    }

    @Override
    public void deleteFile(String path) throws IOException {
        Files.deleteIfExists(Paths.get(path));
        log.info("[로컬저장] 삭제 완료: {}", path);
    }
}
