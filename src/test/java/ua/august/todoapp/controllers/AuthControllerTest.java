package ua.august.todoapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.security.JwtService;
import ua.august.todoapp.services.interfaces.PersonDetailsService;
import ua.august.todoapp.services.interfaces.RegistrationService;
import ua.august.todoapp.util.PersonValidator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(PersonValidator.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private PersonDetailsService personDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationManager authenticationManager;


    @Test
    void shouldReturnBadRequestWhenUsernameIsEmpty() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername("");
        personDTO.setPassword("password");
        personDTO.setConfirmPassword("password");

        mockMvc.perform(post("/api/auth/registration")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());

        verify(registrationService, never()).register(any());
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsEmpty() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername("username");
        personDTO.setPassword("");
        personDTO.setConfirmPassword("password");

        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());

        verify(registrationService, never()).register(any());
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsLessThan3Symbols() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername("username");
        personDTO.setPassword("12");
        personDTO.setConfirmPassword("123456");

        mockMvc.perform(post("/api/auth/registration")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());

        verify(registrationService, never()).register(any());
    }

    @Test
    void shouldReturnBadRequestWhenConfirmedPasswordIsLessThan3Symbols() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername("username");
        personDTO.setPassword("123456");
        personDTO.setConfirmPassword("12");

        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.confirmPassword").exists());

        verify(registrationService, never()).register(any());
    }

    @Test
    void shouldReturnBadRequestWhenPasswordsDoNotMatch() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername("username");
        personDTO.setPassword("123456");
        personDTO.setConfirmPassword("654321");

        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.confirmPassword").exists());

        verify(registrationService, never()).register(any());
    }

    @Test
    void shouldReturnBadRequestWhenUsernameIsTaken() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername("existingUser");
        personDTO.setPassword("123456");
        personDTO.setConfirmPassword("123456");

        when(personDetailsService.existsByUsername("existingUser")).thenReturn(true);

        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());

        verify(registrationService, never()).register(any());
    }

    @Test
    void shouldRegisterAndReturnTokenWhenDataIsValid() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setUsername("newUser");
        personDTO.setPassword("password");
        personDTO.setConfirmPassword("password");

        Person savedPerson = new Person();
        savedPerson.setUsername("newUser");
        savedPerson.setId(1);

        when(personDetailsService.existsByUsername("newUser")).thenReturn(false);
        when(registrationService.register(any(PersonDTO.class))).thenReturn(savedPerson);
        when(jwtService.generateToken(any())).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));

        verify(registrationService).register(any(PersonDTO.class));
    }
}