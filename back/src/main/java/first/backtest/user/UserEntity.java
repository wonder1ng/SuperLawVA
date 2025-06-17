package first.backtest.user;

import first.backtest.util.AriaStringConverter;
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

    @Column(name = "email", unique = true)
    @Convert(converter = AriaStringConverter.class)
    private String email;
}