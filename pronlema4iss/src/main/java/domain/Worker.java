package domain;

import java.util.Objects;

public class Worker {
    private String username, name, role, password;

    public Worker(String username, String name, String role) {
        this.username = username;
        this.name = name;
        this.role = role;
    }

    public Worker(String username, String name, String role, String password) {
        this.username = username;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public Worker() {
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Worker worker)) return false;
        return username.equals(worker.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "Worker{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
