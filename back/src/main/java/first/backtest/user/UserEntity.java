package first.backtest.user;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     private String username;


     private String password;

    @Column(unique = true)
     private String email;
}
