package ru.mephi.prepod.dto;

import lombok.Data;

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
    private Department department;

    @ManyToMany
    @JoinTable(name = "professors_lessons")
    private List<Lesson> lessons;

    @ManyToMany
    @JoinTable(name = "professors_positions")
    private List<Position> positions;
}
