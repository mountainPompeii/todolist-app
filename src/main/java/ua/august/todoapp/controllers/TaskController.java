package ua.august.todoapp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.entity.Priority;
import ua.august.todoapp.entity.Status;
import ua.august.todoapp.services.implementations.PersonDetailsServiceImpl;
import ua.august.todoapp.services.implementations.TaskServiceImpl;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskServiceImpl taskServiceImpl;
    private final PersonDetailsServiceImpl personDetailsServiceImpl;;

    @Autowired
    public TaskController(TaskServiceImpl taskServiceImpl,
                          PersonDetailsServiceImpl personDetailsServiceImpl) {
        this.taskServiceImpl = taskServiceImpl;
        this.personDetailsServiceImpl = personDetailsServiceImpl;
    }

    @GetMapping
    public String index(Model model, Principal principal) {
        Person person = personDetailsServiceImpl.findByUsername(principal.getName());
        List<TaskDTO> tasks = taskServiceImpl.findByOwnerId(person.getId());
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
    public String addTask(@ModelAttribute("task") @Valid TaskDTO taskDTO,
                          BindingResult bindingResult,
                          Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model);
            return "tasks/new";
        }

        Person person = personDetailsServiceImpl.findByUsername(principal.getName());
        taskServiceImpl.save(taskDTO, person);
        return "redirect:/tasks";
    }

    @GetMapping("/{id:[0-9]+}")
    public String show(@PathVariable("id") int id, Model model, Principal principal) {
        Person person = personDetailsServiceImpl.findByUsername(principal.getName());
        TaskDTO taskDTO = taskServiceImpl.findById(id, person.getId());
        model.addAttribute("task", taskDTO);
        return "tasks/show";
    }

    @GetMapping("/{id:[0-9]+}/edit")
    public String editTask(@PathVariable("id") int id, Model model, Principal principal) {
        Person person = personDetailsServiceImpl.findByUsername(principal.getName());
        TaskDTO taskDTO = taskServiceImpl.findById(id, person.getId());
        model.addAttribute("task", taskDTO);
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
        taskDTO.setId(id);
        taskServiceImpl.update(id, taskDTO);
        return "redirect:/tasks";
    }


    @DeleteMapping("/{id:[0-9]+}")
    public String delete(@PathVariable("id") int id) {
        taskServiceImpl.delete(id);
        return "redirect:/tasks";
    }

    private void prepareFormModel(Model model) {
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
    }
}
