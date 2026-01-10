package ua.august.todoapp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.services.implementations.RegistrationServiceImpl;
import ua.august.todoapp.util.PersonValidator;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;
    private final RegistrationServiceImpl registrationServiceImpl;


    @Autowired
    public AuthController(PersonValidator personValidator, RegistrationServiceImpl registrationServiceImpl) {
        this.personValidator = personValidator;
        this.registrationServiceImpl = registrationServiceImpl;
    }


    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person") PersonDTO personDTO) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person") @Valid PersonDTO personDTO,
                                      BindingResult bindingResult) {

        personValidator.validate(personDTO, bindingResult);

        if(bindingResult.hasErrors())
            return "/auth/registration";

        registrationServiceImpl.register(personDTO);

        return "redirect:/auth/login";


    }
}