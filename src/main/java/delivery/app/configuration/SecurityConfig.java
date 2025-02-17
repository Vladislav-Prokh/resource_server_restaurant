package delivery.app.configuration;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.filter.CorsFilter;


@Configuration
public class SecurityConfig {

	@Value("${urls.paths.authServer}")
	private String authServer;
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authz -> authz
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/menu-items/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/menu/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/menu/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/menu/meals").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/menu/desserts").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/orders").hasAnyRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/orders").hasAnyRole("WAITER", "ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/orders").hasAnyRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/reports/**").permitAll()
						.requestMatchers("/menu/beverages").permitAll()
						.requestMatchers("/menu/lunches").permitAll()
						.requestMatchers(HttpMethod.GET, "/elastic/**").permitAll()
						.requestMatchers(HttpMethod.DELETE, "/elastic/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/elastic/**").permitAll()
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
		return NimbusJwtDecoder.withJwkSetUri("http://" +authServer+":9000/oauth2/jwks").build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("http://localhost:4200");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("DELETE");
		config.addAllowedMethod("PATCH");
		config.addAllowedHeader("Content-Type");
		config.addAllowedHeader("Authorization");
		config.addAllowedHeader("X-Requested-With");
		config.addAllowedHeader("Accept");
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);
		source.registerCorsConfiguration("/login", config);
		source.registerCorsConfiguration("/oauth2/**", config);
		source.registerCorsConfiguration("/.well-known/**", config);

		return source;
	}

	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}
}
