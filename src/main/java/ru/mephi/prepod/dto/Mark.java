package ru.mephi.prepod.dto;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "marks")
@Data
public class Mark {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "mark", nullable = false)
    private Character mark;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
