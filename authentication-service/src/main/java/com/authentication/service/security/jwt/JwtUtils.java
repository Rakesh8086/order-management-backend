package com.authentication.service.security.jwt;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.authentication.service.service.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
	private final UserDetailsImpl userPrincipal = null;
	private static final Logger logger = LoggerFactory.getLogger(
			JwtUtils.class);

	@Value("${signup.app.jwtSecret}")
	private String jwtSecret;
	@Value("${signup.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	@Value("${signup.app.jwtCookieName}")
	private String jwtCookie;

	public String getJwtFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, jwtCookie);
		if(cookie != null) {
			return cookie.getValue();
		} 
		else {
			return null;
		}
	}

	public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
		String jwt = generateTokenFromUserId(userPrincipal.getId(), 
				userPrincipal.getEmail(), userPrincipal.getAuthorities());
		ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).
				path("/").maxAge(24 * 60 * 60).httpOnly(true)
				.secure(true).sameSite("None").build();
		return cookie;
	}

	public ResponseCookie getCleanJwtCookie() {
		ResponseCookie cookie = ResponseCookie.from(jwtCookie, "").
				path("/").maxAge(0).httpOnly(true).secure(true)
	            .sameSite("None").build();
		return cookie;
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build()
				.parseClaimsJws(token).getBody().getSubject();
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
			return true;
		} 
		catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} 
		catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} 
		catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} 
		catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public String generateTokenFromUserId(Long userId, 
			String email, Collection<? extends GrantedAuthority> authorities) {
		List<String> roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

		return Jwts.builder()
				.setSubject(String.valueOf(userId))
				.claim("email", email)
				.claim("roles", roles)
				.claim("userId", userId)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key(), SignatureAlgorithm.HS256)
				.compact();
	}
}
