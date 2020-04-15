package com.codeject.ppmtool.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.codeject.ppmtool.domain.User;
import com.codeject.ppmtool.services.CustomUserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static com.codeject.ppmtool.security.SecurityContants.HEADER_PREFIX;
import static com.codeject.ppmtool.security.SecurityContants.TOKEN_PREFIX;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {

		try {

			String jwt = getJWTFromRequest(httpServletRequest);

			if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				Long userId = tokenProvider.getUserIdFromJWT(jwt);
				User userDetails = customUserDetailsService.loadByUserId(userId);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, Collections.emptyList());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				SecurityContextHolder.getContext().setAuthentication(authentication);

			}

		} catch (Exception ex) {
			logger.error("Could not set user authentication in security context", ex);
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);

	}

	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(HEADER_PREFIX);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
			return bearerToken.substring(7, bearerToken.length());
		}

		return null;
	}

}
