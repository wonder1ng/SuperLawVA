package com.superlawva.domain.ocr.service;

import com.superlawva.domain.ocr.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${file.encryption.key:YourSecretKey123YourSecretKey123}")
    private String encryptionKey;
    
    public String saveFile(MultipartFile file) {
        try {
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            
            // 고유한 파일명 생성
            String fileKey = UUID.randomUUID().toString();
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String fileName = fileKey + fileExtension;
            
            // 파일 암호화 및 저장
            byte[] encryptedData = encryptFile(file.getBytes());
            Path targetLocation = uploadPath.resolve(fileName);
            Files.write(targetLocation, encryptedData);
            
            log.info("파일 저장 완료: {}", fileName);
            return fileKey;
            
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 실패", e);
        }
    }
    
    public byte[] loadFile(String fileKey) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileKey + ".enc");
            if (!Files.exists(filePath)) {
                // 다양한 확장자로 시도
                filePath = findFileWithKey(fileKey);
            }
            
            byte[] encryptedData = Files.readAllBytes(filePath);
            return decryptFile(encryptedData);
            
        } catch (IOException e) {
            throw new FileStorageException("파일 로드 실패", e);
        }
    }
    
    private byte[] encryptFile(byte[] data) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                encryptionKey.substring(0, 16).getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new FileStorageException("파일 암호화 실패", e);
        }
    }
    
    private byte[] decryptFile(byte[] encryptedData) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                encryptionKey.substring(0, 16).getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new FileStorageException("파일 복호화 실패", e);
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    private Path findFileWithKey(String fileKey) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        return Files.list(uploadPath)
            .filter(path -> path.getFileName().toString().startsWith(fileKey))
            .findFirst()
            .orElseThrow(() -> new FileStorageException("파일을 찾을 수 없습니다: " + fileKey));
    }
}