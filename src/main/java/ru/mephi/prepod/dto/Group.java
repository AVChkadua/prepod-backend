package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.View;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "groups")
@Data
public class Group {

    @Id
    @Column(name = "id", nullable = false)
    @JsonView(View.Summary.class)
    private String id;

    @Column(name = "number", nullable = false)
    @JsonView(View.Summary.class)
    private String number;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Group parentGroup;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "group")
    private List<Student> students;

    @ManyToMany
    @JoinTable(name = "groups_lessons")
    private List<Lesson> lessons;
}
