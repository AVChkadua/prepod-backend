package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "departments")
@Data
public class Department {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "institute_id", nullable = false)
    @JsonView(Views.Department.Full.class)
    private Institute institute;

    @OneToMany(mappedBy = "department")
    @JsonView(Views.Department.Full.class)
    private List<Professor> professors;

    @OneToMany(mappedBy = "department")
    @JsonView(Views.Department.Full.class)
    private List<Group> groups;
}
