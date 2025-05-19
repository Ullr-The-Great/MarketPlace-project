package es.ullr.proyecto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.service.annotation.HttpExchange;

import es.ullr.proyecto.filter.JwtRequestFilter;
import es.ullr.proyecto.service.MyTokenService;
import es.ullr.proyecto.service.UserDetailsServiceImpl;
import es.ullr.proyecto.util.JwtUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
 
	
	 @Autowired
	 private JwtRequestFilter jwtRequestFilter;
	
	 @Autowired
	 private JwtUtil jwtUtil;
	 
	 @Autowired
	 private MyTokenService tokenService;

		@Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			return http
				.cors().and()
				.csrf(customizer-> customizer.disable())
				.authorizeHttpRequests(request -> request
						.requestMatchers("/api/users/register"
										,"/api/auth/login"
										,"/api/products"
										,"/api/products/{id}"
										,"/api/categories"
										,"/api/auth/me"
										,"/api/products/category/**"
										,"/api/products/search"
										,"/api/categories"
										,"/api/reviews/product/**"
										,"/api/orders/**")

										.permitAll()
						.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
				.logout(customizer -> customizer
						.logoutUrl("/api/auth/logout")
		                .addLogoutHandler((request, response, auth) -> {
		                    String token = jwtUtil.extractToken(request);
		                    tokenService.invalidateToken(token);
		                })
		                .logoutSuccessHandler((request, response, auth) -> {
		                    SecurityContextHolder.clearContext();
		                }))
				.build();
	    }

		@Bean
	    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService) {
	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
			authProvider.setUserDetailsService(userDetailsService);
	        authProvider.setPasswordEncoder(passwordEncoder());
	        return authProvider;
	    }
		
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	   @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	        return authenticationConfiguration.getAuthenticationManager();
	    }
	
	/*//PRUEBAS
	@SuppressWarnings({ "removal", "deprecation" })
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Deshabilitar CSRF
            .authorizeRequests()
                .anyRequest().permitAll(); // Permitir acceso a todos los endpoints sin autenticación
        return http.build();
    }*/
}