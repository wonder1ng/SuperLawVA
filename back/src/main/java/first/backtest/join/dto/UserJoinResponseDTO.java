package first.backtest.join.dto;

import first.backtest.user.UserEntity;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserJoinResponseDTO {
    private Long id;
    private String username;
    private String email;

        public static UserJoinResponseDTO fromEntity(UserEntity user) {
            return UserJoinResponseDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
        }
    }
