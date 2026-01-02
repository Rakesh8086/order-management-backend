package com.billing.service.security;

import java.util.Collection;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.io.Decoders;
import jakarta.servlet.http.Cookie;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${signup.app.jwtSecret}")
    private String jwtSecret;
    @Value("${signup.app.jwtCookieName}")
    private String jwtCookie;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                 .requestMatchers("/api/billing/create/invoice")
                 .access((authentication, context) -> {
                  boolean isFinanceOfficer = authentication.get().getAuthorities().stream()
                   .anyMatch(a -> a.getAuthority().equals("FINANCE_OFFICER")); 
                  String incomingSecret = context.getRequest().getHeader("X-Internal-Secret");
                  // System.out.println("incoming secret ******* " + incomingSecret);
                  // System.out.println("Role is ****** " + isFinanceOfficer);
                  boolean isInternalService = "SYSTEM-CALL".equals(incomingSecret);
                  // System.out.println(isFinanceOfficer || isInternalService);

                  return new AuthorizationDecision(isFinanceOfficer || isInternalService);
              })
                 // other request just need validation of jwt
                 .anyRequest().authenticated()
             )
            .oauth2ResourceServer(oauth2 -> oauth2
                .bearerTokenResolver(cookieTokenResolver()) 
                .jwt(jwt -> jwt.decoder(jwtDecoder())       
                               .jwtAuthenticationConverter(jwtConverter())) 
            );
        return http.build();
    }

    private BearerTokenResolver cookieTokenResolver() {
        return request -> {
            if(request.getCookies() != null){
                for(Cookie c : request.getCookies()){
                    if(jwtCookie.equals(c.getName())){ 
                    	return c.getValue();
                    }
                }
            }
            return null;
        };
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles"); 
        authoritiesConverter.setAuthorityPrefix(""); 

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            System.out.println("JWT Claims found***** " + jwt.getClaims());
            Collection<GrantedAuthority> authorities = authoritiesConverter.convert(jwt);
            System.out.println("Role given******  " + authorities);
            
            return authorities;
        });
        return converter;
    }
}

