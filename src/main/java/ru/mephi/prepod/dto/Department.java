package ru.mephi.prepod.dto;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "departments")
@Data
public class Department {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "institute_id", nullable = false)
    private Institute institute;
}