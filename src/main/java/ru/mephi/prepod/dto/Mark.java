package ru.mephi.prepod.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "marks")
@Data
@NamedEntityGraph(
        name = Mark.ALL_JOINS,
        attributeNodes = {
                @NamedAttributeNode(value = "subject",
                                    subgraph = "subjectLite"),
                @NamedAttributeNode(value = "student",
                                    subgraph = "studentWithGroup")
        },
        subgraphs = {
                @NamedSubgraph(name = "subjectLite", attributeNodes = {}),
                @NamedSubgraph(name = "studentWithGroup", attributeNodes = @NamedAttributeNode("group")),
        }
)
public class Mark {

    public static final String ALL_JOINS = "Mark.allJoins";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "mark", nullable = false)
    private Character mark;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Student student;
}
