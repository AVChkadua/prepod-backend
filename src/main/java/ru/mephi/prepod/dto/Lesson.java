package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.mephi.prepod.LocalDateDeserializer;
import ru.mephi.prepod.LocalDateSerializer;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "lessons")
@Data
@JsonView({ Views.Lesson.Basic.class, Views.Group.Full.class,
            Views.Location.Full.class, Views.Professor.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LessonType lessonType;

    @Column(name = "week", nullable = false)
    @Enumerated(EnumType.STRING)
    private WeekType weekType;

    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_in_semester")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startInSemester;

    @Column(name = "end_in_semester")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endInSemester;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "professors_lessons",
               joinColumns = @JoinColumn(name = "lesson_id"),
               inverseJoinColumns = @JoinColumn(name = "professor_id"))
    @JsonView(Views.Lesson.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Professor> professors;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "groups_lessons",
               joinColumns = @JoinColumn(name = "lesson_id"),
               inverseJoinColumns = @JoinColumn(name = "group_id"))
    @JsonView(Views.Lesson.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Group> groups;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    @JsonView(Views.Lesson.Full.class)
    @EqualsAndHashCode.Exclude
    private Location location;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonView(Views.Lesson.Full.class)
    @EqualsAndHashCode.Exclude
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "start_bell_id", nullable = false)
    private Bell startBell;

    @ManyToOne
    @JoinColumn(name = "end_bell_id", nullable = false)
    private Bell endBell;
}
