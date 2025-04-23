package es.ullr.proyecto.service;

import es.ullr.proyecto.model.User;
import es.ullr.proyecto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService 
{
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
    	 if (user.getUsername() == null || user.getUsername().isEmpty()) {
    	        throw new IllegalArgumentException("Username is required");
    	    }
    	    
    	    if (user.getPassword() == null || user.getPassword().isEmpty()) {
    	        throw new IllegalArgumentException("Password is required");
    	    }
    	    
    	    user.setPassword(passwordEncoder.encode(user.getPassword()));
    	    return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findAllUsers()
    {
    	return userRepository.findAll();
    }
    
    public void deleteById(long id){
    	
    	userRepository.deleteById(id);
    }
    
    
    
}
