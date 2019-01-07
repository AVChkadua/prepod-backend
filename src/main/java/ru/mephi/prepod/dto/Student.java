package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.mephi.prepod.Views;

import javax.persistence.*;

@Entity
@Table(name = "students")
@Data
@JsonView({ Views.Student.Basic.class, Views.Group.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = Student.ALL_JOINS,
                        attributeNodes = {
                                @NamedAttributeNode(value = "group",
                                                    subgraph = "groupLite")
                        },
                        subgraphs = @NamedSubgraph(name = "groupLite", attributeNodes = {})),
                @NamedEntityGraph(name = Student.NO_JOINS)
        }
)
public class Student {

    public static final String ALL_JOINS = "Student.allJoins";
    public static final String NO_JOINS = "Student.noJoins";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "leave_of_absence", nullable = false)
    @JsonView(Views.Student.Full.class)
    private Boolean leaveOfAbsence = false;

    @Column(name = "expelled", nullable = false)
    @JsonView(Views.Student.Full.class)
    private Boolean expelled = false;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonView(Views.Student.Full.class)
    @EqualsAndHashCode.Exclude
    private Group group;
}
