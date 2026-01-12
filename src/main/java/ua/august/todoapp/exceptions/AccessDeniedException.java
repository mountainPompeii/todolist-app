package ua.august.todoapp.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(Integer id) {
        super("You do not have access to task with id " + id);
    }
}
