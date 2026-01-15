package ua.august.todoapp.services.implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.entity.Role;
import ua.august.todoapp.mapper.PersonMapper;
import ua.august.todoapp.repositories.PeopleRepository;
import ua.august.todoapp.services.interfaces.RegistrationService;

@Slf4j
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final PersonMapper personMapper;
    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationServiceImpl(PersonMapper personMapper, PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.personMapper = personMapper;
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Person register(PersonDTO personDTO) {

        log.info("Registering user with username: {}", personDTO.getUsername());

        Person person = personMapper.toPerson(personDTO);

        person.setRole(Role.ROLE_USER);

        String password = personDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        person.setPassword(encodedPassword);

        Person savedPerson = peopleRepository.save(person);

        log.info("New person was registered successfully with username: {}", person.getUsername());

        return savedPerson;
    }
}
