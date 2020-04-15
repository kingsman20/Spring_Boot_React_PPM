package com.codeject.ppmtool.security;

public class SecurityContants {
	
	public static final String SIGN_UP_URLS = "/api/users/**";
	public static final String H2_URL = "h2-console/**";
	public static final String SECRET = "SecretKetToGenerateJWTs";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_PREFIX = "Authorization";
	public static final long EXPIRATION_TIME = 300_000; // 2 minutes

}
