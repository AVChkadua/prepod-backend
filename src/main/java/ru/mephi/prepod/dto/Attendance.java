package ru.mephi.prepod.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Data
@NamedEntityGraph(
        name = Attendance.ALL_JOINS,
        attributeNodes = {
                @NamedAttributeNode(value = "lesson",
                                    subgraph = "lessonLite"),
                @NamedAttributeNode(value = "student",
                                    subgraph = "studentWithGroup")
        },
        subgraphs = {
                @NamedSubgraph(name = "lessonLite", attributeNodes = {}),
                @NamedSubgraph(name = "studentWithGroup", attributeNodes = {
                        @NamedAttributeNode("group")
                })
        }
)

public class Attendance {

    public static final String ALL_JOINS = "Attendance.allJoins";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "is_present", nullable = false)
    private Boolean isPresent;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Student student;
}
