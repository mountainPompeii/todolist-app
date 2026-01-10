package ua.august.todoapp.services.implementations;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.entity.Task;
import ua.august.todoapp.repositories.TaskRepository;
import ua.august.todoapp.services.interfaces.TaskService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task findById(int id) {
        Optional<Task> foundBook = taskRepository.findById(id);
        return foundBook.orElse(null);
    }

    public List<Task> findByOwnerId(Integer ownerId) {
        return taskRepository.findByOwnerId(ownerId);
    }

    @Transactional
    public void save(Task task, Person person) {
        task.setOwner(person);
        taskRepository.save(task);
    }

    @Transactional
    public void update(int id, Task updatedTask) {
        taskRepository.save(updatedTask);
    }

    @Transactional
    public void delete(int id) {
        taskRepository.deleteById(id);
    }
}
