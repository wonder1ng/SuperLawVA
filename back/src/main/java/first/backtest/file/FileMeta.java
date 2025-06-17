package first.backtest.file;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** 파일 메타데이터 – UUID 하나만 클라이언트에 노출 */
@Entity
@Table(name = "file_meta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FileMeta {

    @Id @GeneratedValue
    private UUID id;                // 공개 식별자 (다운로드·삭제용)

    private String originalName;    // 다운로드 시 보여줄 이름
    private String savedPath;       // 내부 경로(암호문 파일 위치)
    private String contentType;
    private Long   size;
}
