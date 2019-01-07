package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "professors")
@Data
@JsonView({ Views.Professor.Basic.class, Views.Department.Full.class,
            Views.Lesson.Full.class, Views.Position.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = Professor.ALL_JOINS,
                        attributeNodes = {
                                @NamedAttributeNode(value = "department",
                                                    subgraph = "departmentLite"),
                                @NamedAttributeNode(value = "lessons",
                                                    subgraph = "lessonLite"),
                                @NamedAttributeNode(value = "positions",
                                                    subgraph = "positionsLite"),
                                @NamedAttributeNode(value = "user",
                                                    subgraph = "userLite")
                        },
                        subgraphs = {
                                @NamedSubgraph(name = "departmentLite", attributeNodes = {}),
                                @NamedSubgraph(name = "lessonLite", attributeNodes = {}),
                                @NamedSubgraph(name = "positionLite", attributeNodes = {}),
                                @NamedSubgraph(name = "userLite", attributeNodes = {})
                        }),
                @NamedEntityGraph(name = Professor.NO_JOINS)
        }
)
public class Professor {

    public static final String ALL_JOINS = "Professor.allJoins";
    public static final String NO_JOINS = "Professor.noJoins";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonView(Views.Professor.Full.class)
    private User user;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @OneToOne
    @JoinColumn(name = "department_id", nullable = false)
    @JsonView(Views.Professor.Full.class)
    @EqualsAndHashCode.Exclude
    private Department department;

    @ManyToMany(mappedBy = "professors")
    @JsonView(Views.Professor.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Lesson> lessons;

    @ManyToMany
    @JoinTable(name = "professors_positions",
               joinColumns = @JoinColumn(name = "professor_id"),
               inverseJoinColumns = @JoinColumn(name = "position_id"))
    @JsonView(Views.Professor.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Position> positions;
}
