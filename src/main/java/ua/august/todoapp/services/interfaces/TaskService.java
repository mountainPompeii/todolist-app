package ua.august.todoapp.services.interfaces;

import ua.august.todoapp.entity.Person;
import ua.august.todoapp.entity.Task;

import java.util.List;

public interface TaskService {
     Task findById(int id);
     List<Task> findByOwnerId(Integer id);
     void save(Task task, Person person);
     void update(int id, Task updatedTask);
     void delete(int id);
}
