package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.View;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "professors")
@Data
public class Professor {

    @Id
    @Column(name = "id", nullable = false)
    @JsonView(View.Summary.class)
    private String id;

    @Column(name = "first_name", nullable = false)
    @JsonView(View.Summary.class)
    private String firstName;

    @Column(name = "middle_name", nullable = false)
    @JsonView(View.Summary.class)
    private String middleName;

    @Column(name = "last_name", nullable = false)
    @JsonView(View.Summary.class)
    private String lastName;

    @OneToOne
    @JoinColumn(name = "departmentId", nullable = false)
    private Department department;

    @ManyToMany
    @JoinTable(name = "professors_lessons")
    private List<Lesson> lessons;

    @ManyToMany
    @JoinTable(name = "professors_positions")
    private List<Position> positions;
}
