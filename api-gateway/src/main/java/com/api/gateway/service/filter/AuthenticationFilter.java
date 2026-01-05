package com.api.gateway.service.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.api.gateway.service.security.JwtUtils;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    @Autowired
    private JwtUtils jwtUtils;
  
    public static final String[] PUBLIC_ENDPOINTS = {
    		"/authentication-service/api/auth/signup", 
    	    "/authentication-service/api/auth/signin", 
    	    "/authentication-service/api/auth/logout",
    	    "/authentication-service/api/auth", 
    	    "/eureka"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();       
        // Check if request path requires authentication
        if(isSecured(request)) {
            String jwt = jwtUtils.getJwtFromCookies(request);
            if(jwt == null) {
                return this.onError(exchange, "Missing JWT token in cookies", HttpStatus.UNAUTHORIZED);
            }
            if(!jwtUtils.validateJwtToken(jwt)) {
                return this.onError(exchange, "Invalid or Expired JWT token", HttpStatus.UNAUTHORIZED);
            }
            String userId = jwtUtils.getUserIdFromJwtToken(jwt); 
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Authenticated-UserId", userId)
                    .build();
            // System.out.println("********Gateway injecting userId: " + userId);
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }
        // valid request or public request
        return chain.filter(exchange);
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        
        return response.setComplete();
    }

    private boolean isSecured(ServerHttpRequest request) {
        String path = request.getURI().getPath();        
        System.out.println(path + "API gateway");
        for(String endpoint : PUBLIC_ENDPOINTS) {
            if(path.startsWith(endpoint)) {
                return false; 
            }
        }
        // Apply security check
        return true; 
    }

    @Override
    public int getOrder() {
        // Run this filter before any routing happens
        return -1; 
    }
}
