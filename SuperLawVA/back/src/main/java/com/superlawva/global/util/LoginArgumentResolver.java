package com.superlawva.global.util;

import com.superlawva.global.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter param) {
        return param.hasParameterAnnotation(LoginUser.class)
                && param.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter param,
                                  ModelAndViewContainer mav,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binder) {

        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();
        // [MOD] JwtUtil#getJwtFromRequest 로 헤더 또는 쿠키에서 토큰 추출
        String token = jwtUtil.getJwtFromRequest(req);

        if (token != null) {
            // [MOD] validateAndGetUserId 호출하여 kakaoId(Long) 반환
            return jwtUtil.validateAndGetUserId(token);
        }
        return null; // 인증 정보가 없으면 null 반환
    }
}
