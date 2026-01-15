package ua.august.todoapp.services.interfaces;

import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.entity.Person;

public interface RegistrationService {
    Person register(PersonDTO personDTO);
}
