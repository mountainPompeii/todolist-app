package ua.august.todoapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ua.august.todoapp.dto.AuthRequestDTO;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.security.JwtService;
import ua.august.todoapp.security.PersonDetails;
import ua.august.todoapp.services.interfaces.RegistrationService;
import ua.august.todoapp.util.PersonValidator;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;
    private final PersonValidator personValidator;
    private final JwtService jwtService;

    @InitBinder("personDTO")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(personValidator);
    }

    @PostMapping("/login")
    public ResponseEntity<?> performLogin(@RequestBody AuthRequestDTO authRequest) {

        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                                                        authRequest.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authInputToken);

            PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(personDetails);

            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect credentials"));
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> performRegistration(@RequestBody @Valid PersonDTO personDTO) {

        Person savedPerson = registrationService.register(personDTO);

        PersonDetails personDetails = new PersonDetails(savedPerson);

        String token = jwtService.generateToken(personDetails);

        return ResponseEntity.ok(Map.of("token", token));

    }
}