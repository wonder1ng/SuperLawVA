package first.backtest.file;

import first.backtest.storage.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService storage;
    private final FileMetaRepository repo;

    /* ---------- 업로드 ---------- */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)   // ✅ ① 추가
    @Operation(summary = "파일 업로드")
    public FileIdDTO upload(
            @Parameter(
                    description = "업로드할 파일",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")  // ✅ ③
                    )
            )
            @RequestPart("file") MultipartFile file                 // ✅ ②
    ) throws Exception {

        /* ── ① 0 byte / 스트림 에러 ───────────────── */
        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "빈 파일");

        /* ── ② MIME 화이트리스트 ─────────────────── */
        if (!List.of("image/png", "image/jpeg", "application/pdf")
                .contains(file.getContentType()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않는 형식");

        /* ── ③ 경로 traversal 방지 ───────────────── */
        Path safe = Paths.get(file.getOriginalFilename()).normalize();
        if (safe.isAbsolute() || safe.toString().contains(".."))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "위험한 파일명");

        /* ── ④ 용량(5 MB) 체크는 서버-레벨로 끝, 메시지만 변경하고 싶으면: ─ */
        if (file.getSize() > 5 * 1024 * 1024)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "5 MB 초과");
//        if (file.getSize() > 5 * 1024 * 1024)
//            throw new RuntimeException("5 MB 초과");
//        if (!List.of("image/png", "image/jpeg", "application/pdf")
//                .contains(file.getContentType()))
//            throw new RuntimeException("허용되지 않는 형식");

        // ① 암호화하여 저장
        String saved = storage.saveBytes(file.getBytes(), file.getOriginalFilename());

        // ② 메타데이터 저장
        FileMeta meta = repo.save(FileMeta.builder()
                .originalName(file.getOriginalFilename())
                .savedPath(saved)
                .contentType(file.getContentType())
                .size(file.getSize())
                .build());

        return new FileIdDTO(meta.getId(), meta.getOriginalName());
    }

    /* ---------- 다운로드 ---------- */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) throws Exception {
        FileMeta meta = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("파일 없음"));

        byte[] plain = storage.loadFile(meta.getSavedPath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, meta.getContentType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                URLEncoder.encode(meta.getOriginalName(), StandardCharsets.UTF_8) + "\"")
                .body(plain);
    }

    /* ---------- 삭제 ---------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws Exception {
        FileMeta meta = repo.findById(id).orElseThrow();
        storage.deleteFile(meta.getSavedPath());
        repo.delete(meta);
        return ResponseEntity.ok().build();
    }

    /* ---------- DTO ---------- */
    public record FileIdDTO(UUID fileId, String name) {}
}
