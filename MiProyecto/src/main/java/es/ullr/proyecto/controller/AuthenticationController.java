package es.ullr.proyecto.controller;

import es.ullr.proyecto.dto.AuthenticationRequest;
import es.ullr.proyecto.model.User;
import es.ullr.proyecto.repository.UserRepository;
import es.ullr.proyecto.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import es.ullr.proyecto.service.MyTokenService;
import es.ullr.proyecto.service.UserDetailsServiceImpl;
import es.ullr.proyecto.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    
    @Autowired
    private MyTokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final User user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        
        final String jwt = jwtUtil.createToken(claims, userDetails.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        
        response.put("token", jwt);
        response.put("user", user);

        return ResponseEntity.ok(response);
	    }catch (BadCredentialsException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
	    }
    }
    
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(){
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	String username = authentication.getName();
    	
    	User user = userRepository.findByUsername(username)
    			.orElseThrow(()-> new RuntimeException("User not found"));
    	
    	return ResponseEntity.ok(user);
    }
    
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        if (token != null) {
            tokenService.invalidateToken(token);
        }
        SecurityContextHolder.clearContext();
    }
}