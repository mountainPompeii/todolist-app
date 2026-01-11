package ua.august.todoapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Task;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {
    @Mapping(target = "owner", ignore = true)
    Task toTask(TaskDTO dto);
    @Mapping(source = "owner.id", target = "ownerId")
    TaskDTO toTaskDTO(Task entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateTaskFromDTO(TaskDTO dto, @MappingTarget Task entity);
}
