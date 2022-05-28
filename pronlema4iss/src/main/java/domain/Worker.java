package domain;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "workers")
public class Worker {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "role")
    private String role;

    @Column(name = "password")
    private String password;

    @OneToOne()
    @JoinColumn(name = "manager")
    private Worker manager;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "logged")
    private boolean logged;

    public Worker(String username, String name, String role) {
        this.username = username;
        this.name = name;
        this.role = role;
    }

    public Worker(String username, String name, String role, Worker manager, String password) {
        this.username = username;
        this.name = name;
        this.role = role;
        this.manager = manager;
        this.password = password;
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

    public void setUsername(String username) {
        this.username = username;
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

    public Worker getManager() {
        return manager;
    }

    public void setManager(Worker manager) {
        this.manager = manager;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean getLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
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
                ", manager=" + manager +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
