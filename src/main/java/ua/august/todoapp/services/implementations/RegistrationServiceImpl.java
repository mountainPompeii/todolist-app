package ua.august.todoapp.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.mapper.PersonMapper;
import ua.august.todoapp.repositories.PeopleRepository;
import ua.august.todoapp.services.interfaces.RegistrationService;

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
    public void register(PersonDTO personDTO) {

        Person person = personMapper.toPerson(personDTO);

        String password = personDTO.getPassword();

        String encodedPassword = passwordEncoder.encode(password);

        person.setPassword(encodedPassword);

        peopleRepository.save(person);
    }
}
