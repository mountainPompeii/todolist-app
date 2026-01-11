package ua.august.todoapp.services.implementations;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.entity.Task;
import ua.august.todoapp.exceptions.TaskNotFoundException;
import ua.august.todoapp.mapper.TaskMapper;
import ua.august.todoapp.repositories.TaskRepository;
import ua.august.todoapp.services.interfaces.TaskService;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService <TaskDTO, Person> {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskDTO findById(int id, Integer ownerId) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        if (task.getOwner() == null || !task.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have access to this task");
        }
        return taskMapper.toTaskDTO(task);
    }

    @Override
    public List<TaskDTO> findByOwnerId(Integer ownerId) {
        List<Task> tasks = taskRepository.findByOwnerId(ownerId);
        return tasks.stream()
                .map(taskMapper::toTaskDTO)
                .toList();
    }

    @Override
    @Transactional
    public void save(TaskDTO taskDTO, Person person) {
        Task task = taskMapper.toTask(taskDTO);
        task.setOwner(person);
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void update(int id, TaskDTO updatedTaskDTO) {
        Task task = taskRepository.findById(id)
                        .orElseThrow(() -> new TaskNotFoundException(id));
        taskMapper.updateTaskFromDTO(updatedTaskDTO, task);
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void delete(int id) {
        taskRepository.deleteById(id);
    }
}
