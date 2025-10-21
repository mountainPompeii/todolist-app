package ua.august.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ua.august.todoapp.entity.Priority;
import ua.august.todoapp.entity.Status;

import java.time.LocalDateTime;


@Data
public class TaskDTO {

    private Integer id;

    @NotBlank(message = "Название задачи не может быть пустым")
    @Size(max = 255, message = "Название задачи не может быть длиннее 255 символов")
    private String title;

    @Size(max = 1000, message = "Описание не может быть длиннее 1000 символов")
    private String description;

    private Status status;
    private Priority priority;
    private LocalDateTime createdAt;

}
