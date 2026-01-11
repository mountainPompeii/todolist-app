package ua.august.todoapp.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ua.august.todoapp.entity.Priority;
import ua.august.todoapp.entity.Status;

import java.time.LocalDateTime;


@Data
public class TaskDTO {

    private Integer id;

    @NotBlank(message = "Task title cannot be empty")
    @Size(max = 255, message = "Task title cannot be longer than 255 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot be longer than 1000 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDateTime createdAt;

    private Integer ownerId;
}
