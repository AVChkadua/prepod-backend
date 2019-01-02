package ru.mephi.prepod.dto;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "lessons")
@Data
public class Lesson {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "type", nullable = false)
    private LessonType lessonType;

    @Column(name = "week", nullable = false)
    private WeekType weekType;

    @Column(name = "start_in_cemester")
    private LocalDate startInCemester;

    @Column(name = "end_in_cemester")
    private LocalDate endInCemester;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "start_bell_id", nullable = false)
    private Bell startBell;

    @ManyToOne
    @JoinColumn(name = "end_bell_id", nullable = false)
    private Bell endBell;
}
