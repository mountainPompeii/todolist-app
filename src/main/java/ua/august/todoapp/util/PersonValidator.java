package ua.august.todoapp.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.services.implementations.PersonDetailsServiceImpl;

import java.util.Objects;

@Component
public class PersonValidator implements Validator {

    private final PersonDetailsServiceImpl personDetailsServiceImpl;

    @Autowired
    public PersonValidator(PersonDetailsServiceImpl personDetailsServiceImpl) {
        this.personDetailsServiceImpl = personDetailsServiceImpl;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonDTO personDTO = (PersonDTO) target;

        if (personDetailsServiceImpl.existsByUsername(personDTO.getUsername())) {
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
