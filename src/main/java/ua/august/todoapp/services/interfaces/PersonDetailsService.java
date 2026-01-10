package ua.august.todoapp.services.interfaces;

import ua.august.todoapp.entity.Person;

public interface PersonDetailsService {
    Person findByUsername(String username);
    boolean existsByUsername(String username);
}
