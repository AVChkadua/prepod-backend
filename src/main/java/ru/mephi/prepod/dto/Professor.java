package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "professors")
@Data
public class Professor {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @OneToOne
    @JoinColumn(name = "departmentId", nullable = false)
    @JsonView(Views.Professor.Full.class)
    private Department department;

    @ManyToMany
    @JoinTable(name = "professors_lessons")
    @JsonView(Views.Professor.Full.class)
    private List<Lesson> lessons;

    @ManyToMany
    @JoinTable(name = "professors_positions")
    @JsonView(Views.Professor.Full.class)
    private List<Position> positions;
}
