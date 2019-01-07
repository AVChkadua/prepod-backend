package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.Views;

import javax.persistence.*;

@Entity
@Table(name = "subjects")
@Data
@JsonView({ Views.Subject.class, Views.Lesson.Basic.class, Views.Group.Full.class,
            Views.Location.Full.class, Views.Professor.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;
}
