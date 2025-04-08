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
import es.ullr.proyecto.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
 
	
	 @Autowired
	 private JwtRequestFilter jwtRequestFilter;

		@Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	       /* http
	            .authorizeRequests()
	                .requestMatchers("/api/auth/login").permitAll() // Permitir acceso público a /api/auth/**
	                .requestMatchers("/api/users/register").permitAll() // Permitir registro de usuarios
	                .requestMatchers("/error").permitAll()
	                .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
	            .and()
	            .httpBasic().and()
	            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
	            .csrf().disable(); // No usar sesiones

	       http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);*/
			
			return http
				.cors().and()
				.csrf(customizer-> customizer.disable())
				.authorizeHttpRequests(request -> request
						.requestMatchers("/api/users/register"
										,"/api/auth/login"
										,"/api/products"
										,"/api/categories"
										,"/api/auth/me")
										.permitAll()
						.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
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