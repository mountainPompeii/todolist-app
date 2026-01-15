package ua.august.todoapp.services.interfaces;

import org.springframework.security.core.userdetails.UserDetailsService;
import ua.august.todoapp.entity.Person;

public interface PersonDetailsService extends UserDetailsService {
    Person findByUsername(String username);
    boolean existsByUsername(String username);
}
