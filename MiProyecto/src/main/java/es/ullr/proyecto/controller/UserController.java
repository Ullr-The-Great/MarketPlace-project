package es.ullr.proyecto.controller;

import es.ullr.proyecto.model.User;
import es.ullr.proyecto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController 
{

    @Autowired
    private UserService userService;

    /*// Crear un usuario
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) 
    {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }*/

    // Obtener un usuario por nombre de usuario
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) 
    {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Obtener un usuario por correo electrónico
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) 
    {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<User>> findAllUsers() 
    {
        List<User> listUsers = userService.findAllUsers();
        return new ResponseEntity<>(listUsers, HttpStatus.OK);
    }
    
    // Registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        user.setRole("ADMIN"); // Asignar rol por defecto
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUserbyId(@PathVariable long id){
    	
    	userService.deleteById(id);
    	return new ResponseEntity<>(HttpStatus.OK);
    	
    }
    
    
}