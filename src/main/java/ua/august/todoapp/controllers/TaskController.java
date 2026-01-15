package ua.august.todoapp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Priority;
import ua.august.todoapp.entity.Status;
import ua.august.todoapp.security.PersonDetails;
import ua.august.todoapp.services.interfaces.TaskService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDTO> index(@AuthenticationPrincipal PersonDetails personDetails) {
        return taskService.findByOwnerId(personDetails.getPerson().getId());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<TaskDTO> show(@PathVariable("id") int id,
                                         @AuthenticationPrincipal PersonDetails personDetails) {
        TaskDTO taskDTO = taskService.findById(id, personDetails.getPerson().getId());
        return ResponseEntity.ok(taskDTO);
    }

    @GetMapping("/metadata")
    public ResponseEntity<Map<String, Object>> getMetaData() {
        return ResponseEntity.ok(Map.of(
                "priorities", Priority.values(),
                "statuses", Status.values()
        ));
    }

    @PostMapping
    public ResponseEntity<?> addTask(@RequestBody @Valid TaskDTO taskDTO,
                                     @AuthenticationPrincipal PersonDetails personDetails) {
        taskService.save(taskDTO, personDetails.getPerson());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id,
                         @RequestBody @Valid TaskDTO taskDTO,
                         @AuthenticationPrincipal PersonDetails personDetails) {
        taskDTO.setId(id);
        int currentOwnerId = personDetails.getPerson().getId();
        taskService.update(id, taskDTO, currentOwnerId);

        return ResponseEntity.ok(Map.of("message", "Task updated successfully"));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id,
                         @AuthenticationPrincipal PersonDetails personDetails) {
        int currentOwnerId = personDetails.getPerson().getId();
        taskService.delete(id, currentOwnerId);

        return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
    }

}
