package ua.august.todoapp.entity;

public enum Status {
    TODO("Новая"),
    IN_PROGRESS("В процессе"),
    DONE("Сделана");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}