package ua.august.todoapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.entity.Person;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapper {
        Person toPerson(PersonDTO dto);
        PersonDTO toPersonDTO(Person entity);
}

