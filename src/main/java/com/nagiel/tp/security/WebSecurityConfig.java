package com.nagiel.tp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import com.nagiel.tp.security.jwt.AuthEntryPointJwt;
import com.nagiel.tp.security.jwt.AuthTokenFilter;
import com.nagiel.tp.service.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

	@Value("${spring.h2.console.path}")
	private String h2ConsolePath;

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.cors(c -> c.configurationSource(request -> {

					CorsConfiguration cors = new CorsConfiguration();
					String[] allowedOrigins = { "https://localhost", "http://localhost:8100" };
					String[] exposedHeaders = { HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
							HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN };

					cors.applyPermitDefaultValues();
					cors.setAllowedOrigins(List.of(allowedOrigins));
					cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
					cors.setAllowedHeaders(List.of("*"));
					cors.setExposedHeaders(List.of(exposedHeaders));
					cors.setAllowCredentials(true);
					cors.setMaxAge(3600L);
					return cors;

				})).authorizeHttpRequests(
						auth -> auth
						.requestMatchers("/h2-ui/**").permitAll()
						.requestMatchers(PathRequest.toH2Console()).permitAll()
						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/api/articles").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/article/**").permitAll()
						.requestMatchers("/api/test/all").permitAll()
						.anyRequest().authenticated());

		http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
		http.authenticationProvider(authenticationProvider());
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
