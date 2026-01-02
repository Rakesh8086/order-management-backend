package com.order.service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
@EnableWebSecurity
@EnableMethodSecurity
public class FeignInterceptor implements RequestInterceptor {
    @Value("${signup.app.jwtCookieName}")
    private String jwtCookie;
    
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) 
        		RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Cookie[] cookies = request.getCookies();         
            if(cookies != null) {
                for(Cookie cookie : cookies) {
                    if(jwtCookie.equals(cookie.getName())) {
                        template.header("Cookie", 
                        		cookie.getName() + "=" + cookie.getValue());
                    }
                }
            }
        }
    }
}
