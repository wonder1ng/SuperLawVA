package first.backtest.login_out.service;

import first.backtest.jwt.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import first.backtest.login_out.dto.LoginRequestDTO;
import first.backtest.login_out.dto.LoginResponseDTO;
import first.backtest.user.UserEntity;
import first.backtest.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LogIn_OutService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil; // JWT 토큰 생성/검증 유틸리티

    /**
     * 사용자 로그인 처리
     * @param loginRequest 로그인 요청 (사용자명, 비밀번호)
     * @return 로그인 응답 (사용자 정보 + JWT 토큰)
     */
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // 1. 사용자 인증 (존재 여부 + 비밀번호 검증)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),    // 사용자명
                            loginRequest.getPassword()     // 비밀번호
                    )
            );

            // 2. 인증 통과 후 사용자 정보 조회 (추가 정보 활용 목적, 예외 없이 .get() 사용)
            // authentication을 통과했기 때문에 존재 보장됨
            UserEntity user = userRepository.findByUsername(loginRequest.getUsername()).get();

            // 3. 사용자명을 기반으로 JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getUsername());

            // 4. 사용자 정보 + 토큰을 DTO로 감싸서 반환
            return LoginResponseDTO.fromEntity(user, token);

        } catch (BadCredentialsException e) {
            // 인증 실패 시 예외 메시지를 명확히 전달
            throw new RuntimeException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    /**
     * 사용자 로그아웃 처리
     * JWT 방식은 서버에 상태를 저장하지 않기 때문에 별도 처리 없음
     */
    public void logout() {
        // 클라이언트가 JWT 토큰을 삭제하는 것으로 로그아웃 처리됨
        // 서버 측 상태 유지 불필요
    }
}

