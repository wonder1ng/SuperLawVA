package com.study.MyLilLawyer.util;

import com.study.MyLilLawyer.global.security.util.JwtUtil;
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
        return param.hasParameterAnnotation(LoginUser.class) &&
                param.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter      param,
                                  ModelAndViewContainer mav,
                                  NativeWebRequest      webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binder) {

        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();
        String bearer = req.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return jwtUtil.validateAndGetUserId(bearer.substring(7)); // kakaoId(Long)
        }
        return null; // 인증 안 됨
    }
}
