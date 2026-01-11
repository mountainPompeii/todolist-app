package ua.august.todoapp.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.services.implementations.PersonDetailsServiceImpl;
import ua.august.todoapp.services.interfaces.PersonDetailsService;

import java.util.Objects;

@Component
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;

    @Autowired
    public PersonValidator(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonDTO personDTO = (PersonDTO) target;

        if (personDetailsService.existsByUsername(personDTO.getUsername())) {
            errors.rejectValue("username",
                    "",
                    "Username is already taken");
        }

        if (!Objects.equals(personDTO.getPassword(), personDTO.getConfirmPassword())) {
            errors.rejectValue("confirmPassword",
                    "password.mismatch",
                    "Passwords do not match!");
        }
    }
}
