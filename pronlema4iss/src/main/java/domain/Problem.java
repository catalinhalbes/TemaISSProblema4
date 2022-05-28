package domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "problems")
public class Problem {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "task")
    private Task task;

    public Problem() {
    }

    public Problem(Long id, String description, LocalDateTime date, Task task) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.task = task;
    }

    public Problem(String description, LocalDateTime date, Task task) {
        this.description = description;
        this.date = date;
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", task=" + task +
                '}';
    }
}
