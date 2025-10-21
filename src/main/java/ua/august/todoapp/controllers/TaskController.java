package ua.august.todoapp.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Priority;
import ua.august.todoapp.entity.Status;
import ua.august.todoapp.entity.Task;
import ua.august.todoapp.services.TaskService;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ModelMapper modelMapper;

    @Autowired
    public TaskController(TaskService taskService,
                          ModelMapper modelMapper) {
        this.taskService = taskService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tasks", taskService.findAll());
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
                          Model model) {
        if (bindingResult.hasErrors()) {
            prepareFormModel(model);
            return "tasks/new";
        }
        taskService.save(convertToEntity(taskDTO));
        return "redirect:/tasks";
    }

    @GetMapping("/{id:[0-9]+}")
    public String show(@PathVariable("id") int id, Model model) {
        Task task = taskService.findById(id);
        model.addAttribute("task", convertToTaskDTO(task));
        return "tasks/show";
    }

    @GetMapping("/{id:[0-9]+}/edit")
    public String editTask(@PathVariable("id") int id, Model model) {
        Task task = taskService.findById(id);
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
        taskService.update(id, task);
        return "redirect:/tasks";
    }


    @DeleteMapping("/{id:[0-9]+}")
    public String delete(@PathVariable("id") int id) {
        taskService.delete(id);
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
