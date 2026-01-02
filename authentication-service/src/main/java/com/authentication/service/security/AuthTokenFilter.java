package com.authentication.service.security;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.authentication.service.entity.User;
import com.authentication.service.repository.UserRepository;
import com.authentication.service.security.jwt.JwtUtils;
import com.authentication.service.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private UserRepository userRepository;
	private static final String PUBLIC_PATH_PATTERN = "/api/auth/**";
	private static final String SERVICE_ID_PREFIX = "/authentication-service";
	private final AntPathMatcher pathMatcher = new AntPathMatcher();
	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestUri = request.getRequestURI();
		String pathToCheck = requestUri;
		System.out.println(requestUri);
		if (pathToCheck.startsWith(SERVICE_ID_PREFIX)) {
			// Strip the /authentication-service prefix
			pathToCheck = pathToCheck.substring(SERVICE_ID_PREFIX.length());
		}
		System.out.println(pathToCheck);
		if (!pathToCheck.startsWith("/")) {
			pathToCheck = "/" + pathToCheck;
		}

		if (pathMatcher.match(PUBLIC_PATH_PATTERN, pathToCheck)) {
			// If the path is public, skip token processing
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);
				Optional<User> userOptional = userRepository.findById(Long.valueOf(username));
			    String mobileNumber = userOptional.get().getMobileNumber();
			    // System.err.println("****** " + username);
			    // System.err.println("###### " + mobileNumber);
			    UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNumber);		    

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String jwt = jwtUtils.getJwtFromCookies(request);
		return jwt;
	}
}