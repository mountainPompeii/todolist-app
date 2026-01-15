package ua.august.todoapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.entity.Priority;
import ua.august.todoapp.entity.Status;
import ua.august.todoapp.exceptions.AccessDeniedException;
import ua.august.todoapp.security.PersonDetails;
import ua.august.todoapp.services.interfaces.PersonDetailsService;
import ua.august.todoapp.services.interfaces.TaskService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private PersonDetailsService personDetailsService;

    private PersonDetails testPersonDetails;

    @BeforeEach
    public void setup() {
        Person person = new Person();
        person.setId(2);
        person.setUsername("test");
        person.setPassword("password");
        testPersonDetails = new PersonDetails(person);
    }

    @Test
    void shouldReturn403WhenDeletingNotOwnedTask () throws Exception {
        // arrange
        doThrow(new AccessDeniedException(1))
                .when(taskService).delete(eq(1), eq(2));

        // act and assert
        mockMvc.perform(delete("/api/tasks/1")
                        .with(user(testPersonDetails))
                        .with(csrf()))
                .andExpect(status().isForbidden());

    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        // act
        mockMvc.perform(delete("/api/tasks/1")
                        .with(user(testPersonDetails))
                        .with(csrf()))
                // assert
                .andExpect(status().isOk());


        verify(taskService).delete(eq(1), eq(2));
    }

    @Test
    void shouldReturn403WhenUpdatingNotOwnedTask () throws Exception {
        // arrange
        TaskDTO taskError = new TaskDTO();
        taskError.setTitle("test");
        taskError.setDescription("test");
        taskError.setPriority(Priority.HIGH);
        taskError.setStatus(Status.DONE);
        taskError.setOwnerId(1);

        doThrow(new AccessDeniedException(1))
                .when(taskService).update(eq(1), any(), eq(2));

        // act and assert
        mockMvc.perform(patch("/api/tasks/1")
                        .with(user(testPersonDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskError)))
                        .andExpect(status().isForbidden());
    }
    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        // arrange
        TaskDTO updateData = new TaskDTO();
        updateData.setTitle("title1");
        updateData.setDescription("description1");
        updateData.setPriority(Priority.LOW);
        updateData.setStatus(Status.DONE);

        // act
        mockMvc.perform(patch("/api/tasks/1")
                        .with(user(testPersonDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString((updateData))))
                // assert
                .andExpect(status().isOk());

        verify(taskService).update(eq(1), any(), eq(2));
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        // arrange
        when(personDetailsService.findByUsername(testPersonDetails.getUsername()))
                .thenReturn(testPersonDetails.getPerson());

        TaskDTO newTask = new TaskDTO();
        newTask.setTitle("title");
        newTask.setDescription("description");
        newTask.setPriority(Priority.LOW);
        newTask.setStatus(Status.DONE);


        // act
        mockMvc.perform(post("/api/tasks")
                        .with(user(testPersonDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                // assert
                .andExpect(status().isOk());

        verify(taskService).save(any(), eq(testPersonDetails.getPerson()));
    }

    @Test
    void shouldReturnTaskListJson() throws Exception {

        // arrange
        when(personDetailsService.findByUsername(testPersonDetails.getUsername()))
                .thenReturn(testPersonDetails.getPerson());

        TaskDTO newTask = new TaskDTO();
        newTask.setId(1);
        newTask.setTitle("title");
        newTask.setDescription("description");
        newTask.setPriority(Priority.LOW);
        newTask.setStatus(Status.DONE);

        when(taskService.findByOwnerId(testPersonDetails.getPerson().getId()))
                .thenReturn(List.of(newTask));

        // act
        mockMvc.perform(get("/api/tasks")
                        .with(user(testPersonDetails)))
                // assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("title"));


        verify(taskService).findByOwnerId(testPersonDetails.getPerson().getId());
    }

    @Test
    void shouldReturnBadRequestWhenCreatingInvalidTask() throws Exception {
        // arrange
        TaskDTO taskError1 = new TaskDTO();
        taskError1.setTitle("");
        // act
        mockMvc.perform(post("/api/tasks")
                        .with(user(testPersonDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskError1)))
                // assert
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());

        verify(taskService, never()).save(any(), any());
    }

    @Test
    void shouldShowTaskDetails() throws Exception {
        // arrange
        TaskDTO mockTask = new TaskDTO();
        mockTask.setId(1);
        mockTask.setTitle("test");

        when(personDetailsService.findByUsername(testPersonDetails.getUsername()))
                .thenReturn(testPersonDetails.getPerson());

        when(taskService.findById(eq(1), eq(testPersonDetails.getPerson().getId())))
                .thenReturn(mockTask);

        // act
        mockMvc.perform(get("/api/tasks/1")
                        .with(user(testPersonDetails)))
                // assert
                .andExpect(status().isOk());

        verify(taskService).findById(eq(1), eq(testPersonDetails.getPerson().getId()));
    }

}