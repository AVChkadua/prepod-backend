package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.View;

import javax.persistence.*;

@Entity
@Table(name = "students")
@Data
public class Student {

    @Id
    @Column(name = "id", nullable = false)
    @JsonView(View.Summary.class)
    private String id;

    @Column(name = "name", nullable = false)
    @JsonView(View.Summary.class)
    private String name;

    @Column(name = "leave_of_absence", nullable = false)
    private Boolean leaveOfAbsence;

    @Column(name = "expelled", nullable = false)
    private Boolean expelled;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
