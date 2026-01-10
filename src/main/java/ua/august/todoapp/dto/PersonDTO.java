package ua.august.todoapp.dto;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ua.august.todoapp.entity.Task;

import java.util.List;

@Data
public class PersonDTO {

    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Task> tasks;
}
