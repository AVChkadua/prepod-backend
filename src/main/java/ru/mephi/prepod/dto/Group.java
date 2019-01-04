package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "groups")
@Data
public class Group {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "number", nullable = false)
    private String number;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonView(Views.Group.Full.class)
    private Group parentGroup;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @JsonView(Views.Group.Full.class)
    private Department department;

    @OneToMany(mappedBy = "group")
    @JsonView(Views.Group.Full.class)
    private List<Student> students;

    @ManyToMany
    @JoinTable(name = "groups_lessons")
    @JsonView(Views.Group.Full.class)
    private List<Lesson> lessons;
}
