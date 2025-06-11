package com.superlawva.global.util;

import java.lang.annotation.*;

/**
 * 컨트롤러 메서드 파라미터에 kakaoId(Long)을 주입받기 위한 커스텀 어노테이션
 * 예) public String myPage(@LoginUser Long kakaoId) { ... }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser { }
