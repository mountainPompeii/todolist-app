package ua.august.todoapp.services.interfaces;

import java.util.List;

public interface TaskService<T,P> {
     T findById(int id, Integer ownerId);
     List<T> findByOwnerId(Integer id);
     void save(T task, P owner);
     void update(int id, T updatedTask);
     void delete(int id);
}
