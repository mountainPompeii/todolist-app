package ua.august.todoapp.entity;

public enum Status {
    TODO("New"),
    IN_PROGRESS("In Progress"),
    DONE("Completed"),;

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}