package ua.august.todoapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.august.todoapp.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
}
