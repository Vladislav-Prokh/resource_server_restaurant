package delivery.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authz -> authz
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/api/auth/logout").permitAll()
						.requestMatchers("/api/auth/userinfo").permitAll()
						.requestMatchers(HttpMethod.POST, "/menu/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/menu/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/menu/meals").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/menu/desserts").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/orders").hasAnyRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("WAITER", "ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/orders").hasAnyRole("ADMIN")
						.requestMatchers("/employees").hasRole("ADMIN")
						.requestMatchers("/menu/beverages").permitAll()
						.requestMatchers("/menu/lunches").permitAll()
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt
								.jwtAuthenticationConverter(jwtAuthenticationConverter())
						)
				);
		return http.build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
		grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withJwkSetUri("http://localhost:9000/oauth2/jwks").build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.addAllowedOrigin("http://localhost:4200");
		config.setAllowCredentials(true);
		source.registerCorsConfiguration("/**", config);
		return source;
	}

}
