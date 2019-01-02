package ru.mephi.prepod.dto;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "students")
@Data
public class Student {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "leave_of_absence", nullable = false)
    private Boolean leaveOfAbsence;

    @Column(name = "expelled", nullable = false)
    private Boolean expelled;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
