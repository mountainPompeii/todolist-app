package ua.august.todoapp.services.implementations;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.repositories.PeopleRepository;
import ua.august.todoapp.services.interfaces.RegistrationService;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final ModelMapper modelMapper;
    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationServiceImpl(ModelMapper modelMapper, PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(PersonDTO personDTO) {

        Person person = modelMapper.map(personDTO, Person.class);

        String password = personDTO.getPassword();

        String encodedPassword = passwordEncoder.encode(password);

        person.setPassword(encodedPassword);

        peopleRepository.save(person);
    }
}
