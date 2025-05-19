package es.ullr.proyecto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ullr.proyecto.model.User;

public interface UserRepository extends JpaRepository<User, Long>
{
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
}
