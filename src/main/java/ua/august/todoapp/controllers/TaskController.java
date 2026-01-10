package ua.august.todoapp.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.entity.Priority;
import ua.august.todoapp.entity.Status;
import ua.august.todoapp.entity.Task;
import ua.august.todoapp.services.implementations.PersonDetailsServiceImpl;
import ua.august.todoapp.services.implementations.TaskServiceImpl;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskServiceImpl taskServiceImpl;
    private final PersonDetailsServiceImpl personDetailsServiceImpl;
    private final ModelMapper modelMapper;

    @Autowired
    public TaskController(TaskServiceImpl taskServiceImpl,
                          ModelMapper modelMapper, PersonDetailsServiceImpl personDetailsServiceImpl) {
        this.taskServiceImpl = taskServiceImpl;
        this.modelMapper = modelMapper;
        this.personDetailsServiceImpl = personDetailsServiceImpl;
    }

    @GetMapping
    public String index(Model model, Principal principal) {
        Person person = personDetailsServiceImpl.findByUsername(principal.getName());
        List<Task> tasks = taskServiceImpl.findByOwnerId(person.getId());
        model.addAttribute("tasks", tasks);
        return "tasks/index";
    }

    @GetMapping("/new")
    public String newTask(Model model) {
        model.addAttribute("task", new TaskDTO());
        prepareFormModel(model);
        return "tasks/new";
    }

    @PostMapping
    public String addTask(@ModelAttribute("task") @Valid Task task,
                          BindingResult bindingResult,
                          Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model);
            return "tasks/new";
        }

        Person person = personDetailsServiceImpl.findByUsername(principal.getName());
        taskServiceImpl.save(task, person);
        return "redirect:/tasks";
    }

    @GetMapping("/{id:[0-9]+}")
    public String show(@PathVariable("id") int id, Model model, Principal principal) {

        Task task = taskServiceImpl.findById(id);

        Person person = personDetailsServiceImpl.findByUsername(principal.getName());
        if (task.getOwner() == null || !task.getOwner().getId().equals(person.getId())) {
            throw new AccessDeniedException("У вас нет доступа к этой задаче");
        }

        model.addAttribute("task", task);
        return "tasks/show";
    }

    @GetMapping("/{id:[0-9]+}/edit")
    public String editTask(@PathVariable("id") int id, Model model) {
        Task task = taskServiceImpl.findById(id);
        model.addAttribute("task", convertToTaskDTO(task));
        prepareFormModel(model);
        return "tasks/edit";
    }

    @PatchMapping("/{id:[0-9]+}")
    public String update(@PathVariable("id") int id,
                         @ModelAttribute("task") @Valid TaskDTO taskDTO,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model);
            return "tasks/edit";
        }
        Task task = convertToEntity(taskDTO);
        task.setId(id);
        taskServiceImpl.update(id, task);
        return "redirect:/tasks";
    }


    @DeleteMapping("/{id:[0-9]+}")
    public String delete(@PathVariable("id") int id) {
        taskServiceImpl.delete(id);
        return "redirect:/tasks";
    }

    private Task convertToEntity(TaskDTO taskDTO) {
        return  modelMapper.map(taskDTO, Task.class);
    }
    private TaskDTO convertToTaskDTO(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }
    private void prepareFormModel(Model model) {
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
    }
}
