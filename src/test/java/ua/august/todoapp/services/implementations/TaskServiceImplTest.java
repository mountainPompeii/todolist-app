package ua.august.todoapp.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.*;
import ua.august.todoapp.exceptions.AccessDeniedException;
import ua.august.todoapp.exceptions.TaskNotFoundException;
import ua.august.todoapp.mapper.TaskMapper;
import ua.august.todoapp.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;
    private Task task;
    private Person person;
    private TaskDTO taskDTO;


    @BeforeEach
    void setUp() {
        List<Task> tasks = new ArrayList<>();
        person = new Person(1, "Test_Username", "Test_Password", Role.ROLE_USER, tasks);
        task = new Task(1, "Title", "Description", Status.DONE, Priority.HIGH, person, LocalDateTime.now());
        taskDTO = new TaskDTO(1 , "Title", "Description", Status.DONE, Priority.HIGH, LocalDateTime.now(), 1);
    }

    @Test
    @DisplayName("Testing method findById if Task is exists")
    void shouldReturnTaskWhenExists() {

        // arrange
        when(taskMapper.toTaskDTO(task)).thenReturn(taskDTO);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        // act
        TaskDTO result = taskServiceImpl.findById(1,1);

        // assert
        verify(taskRepository).findById(task.getId());
        assertAll(
                () -> assertEquals(1, result.getId()),
                () -> assertEquals(1, result.getOwnerId()),
                () -> assertEquals("Title", result.getTitle()),
                () -> assertEquals("Description", result.getDescription())
        );
    }

    @Test
    @DisplayName("Testing method findByOwnerId if Owner is exists")
    void shouldReturnTasksWhenExistsWithOwner() {

        // arrange
        when(taskMapper.toTaskDTO(task)).thenReturn(taskDTO);
        when(taskRepository.findByOwnerId(task.getOwner().getId())).thenReturn(List.of(task));

        // act
        List<TaskDTO> result = taskServiceImpl.findByOwnerId(task.getOwner().getId());

        // assert
        verify(taskRepository).findByOwnerId(task.getOwner().getId());
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1, result.get(0).getOwnerId()),
                () -> assertEquals("Title", result.get(0).getTitle())
        );
    }

    @Test
    @DisplayName("Testing method findById if Task is doesn't exist")
    void shouldThrowExceptionWhenDoesNotExists() {

        // arrange
        when(taskRepository.findById(666)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(TaskNotFoundException.class,
                () -> taskServiceImpl.findById(666,1));
    }

    @Test
    @DisplayName("Testing method findById if Task doesn't belong to User")
    void shouldThrowExceptionWhenDoesNotBelongToUser() {

        task.setOwner(new  Person(2, "Test_Username", "Test_Password", Role.ROLE_USER, List.of()));

        // arrange
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        // act and assert
        assertThrows(AccessDeniedException.class,
                () -> taskServiceImpl.findById(task.getId(),1));
    }

    @Test
    @DisplayName("Testing method save")
    void shouldSaveTask() {
        // arrange
        when(taskMapper.toTask(taskDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);

        // act
        taskServiceImpl.save(taskDTO, person);

        // assert
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("Testing method update")
    void shouldUpdateTask() {

        // arrange
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        // act
        taskServiceImpl.update(task.getId(), taskDTO, task.getOwner().getId());

        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("Testing method update: throws exception when not owner")
    void shouldThrowExceptionWhenUpdateNotOwner() {
        // arrange
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        // act and assert
        assertThrows(AccessDeniedException.class,
                () -> taskServiceImpl.update(task.getId(), taskDTO, 55));

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Testing method delete")
    void shouldDeleteTask() {
        // arrange
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        // act
        taskServiceImpl.delete(task.getId(), task.getOwner().getId());

        // assert
        verify(taskRepository).findById(task.getId());
        verify(taskRepository).deleteById(task.getId());
    }

    @Test
    @DisplayName("Testing method delete: throws exception when not owner")
    void shouldThrowExceptionWhenDeleteNotOwnedTask() {
        // arrange
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        // act and assert
        assertThrows(AccessDeniedException.class,
                () -> taskServiceImpl.delete(task.getId(), 666));

        verify(taskRepository, never()).deleteById(anyInt());
    }
}