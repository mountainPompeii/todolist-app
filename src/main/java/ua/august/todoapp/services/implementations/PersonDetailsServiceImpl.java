package ua.august.todoapp.services.implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.repositories.PeopleRepository;
import ua.august.todoapp.security.PersonDetails;
import ua.august.todoapp.services.interfaces.PersonDetailsService;
import java.util.Optional;

@Slf4j
@Service
public class PersonDetailsServiceImpl implements UserDetailsService, PersonDetailsService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonDetailsServiceImpl(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;

    }

    @Override
    public Person findByUsername(String username){
        log.info("Finding user with username: {}", username);
        return peopleRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
       Optional<Person> person = peopleRepository.findByUsername(username);

       if (person.isEmpty()) {
           throw new UsernameNotFoundException("User not found!");
       }

       return new PersonDetails(person.get());

    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("Checking if username exists: {}", username);
        return peopleRepository.existsByUsername(username);
    }

}
