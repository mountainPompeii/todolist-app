package ua.august.todoapp.services.interfaces;

import ua.august.todoapp.entity.Task;

import java.util.List;

public interface TaskService<T,P> {
     Task findById(int id);
     List<Task> findByOwnerId(Integer id);
     void save(T task, P owner);
     void update(int id, T updatedTask);
     void delete(int id);
}
