package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.Views;

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
    @JsonView(Views.Student.Full.class)
    private Boolean leaveOfAbsence;

    @Column(name = "expelled", nullable = false)
    @JsonView(Views.Student.Full.class)
    private Boolean expelled;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonView(Views.Student.Full.class)
    private Group group;
}
