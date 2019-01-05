package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "lessons")
@Data
public class Lesson {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "type", nullable = false)
    private LessonType lessonType;

    @Column(name = "week", nullable = false)
    private WeekType weekType;

    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_in_semester")
    private LocalDate startInSemester;

    @Column(name = "end_in_semester")
    private LocalDate endInSemester;

    @ManyToMany(mappedBy = "lessons")
    @JsonView(Views.Lesson.Full.class)
    private List<Professor> professors;

    @ManyToMany(mappedBy = "lessons")
    @JsonView(Views.Lesson.Full.class)
    private List<Group> groups;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    @JsonView(Views.Lesson.Full.class)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonView(Views.Lesson.Full.class)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "start_bell_id", nullable = false)
    private Bell startBell;

    @ManyToOne
    @JoinColumn(name = "end_bell_id", nullable = false)
    private Bell endBell;
}
