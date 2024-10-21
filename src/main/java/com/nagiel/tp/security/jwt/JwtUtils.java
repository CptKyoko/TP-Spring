package com.nagiel.tp.security.jwt;

import java.security.Key;
import java.util.Date;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.nagiel.tp.service.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${tp.app.jwtSecret}")
	private String jwtSecret;

	@Value("${tp.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Value("${tp.app.jwtCookieName}")
	private String jwtCookie;

	public String getJwtFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, jwtCookie);
		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
	    String jwt = generateTokenFromUsername(userPrincipal.getUsername());
	    ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
	        .path("/") // Changer le chemin en "/" pour qu'il soit accessible partout
	        .maxAge(24 * 60 * 60) // Durée de vie de 24 heures
	        .httpOnly(true) // Empêche l'accès JavaScript
	        .secure(true) // S'assurer que cela soit vrai si tu es en HTTPS
	        .sameSite("None") // Permettre le partage cross-origin
	        .build();
	    return cookie;
	}

	public ResponseCookie getCleanJwtCookie() {
	    ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
	        .path("/") // S'assurer que le chemin est "/" pour qu'il soit accessible
	        .maxAge(0) // Durée de vie nulle pour supprimer le cookie
	        .httpOnly(true)
	        .secure(true) // Assurez-vous que cela soit vrai si tu es en HTTPS
	        .sameSite("None") // Permettre le partage cross-origin
	        .build();
	    return cookie;
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public String generateTokenFromUsername(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key(), SignatureAlgorithm.HS256).compact();
	}
}
