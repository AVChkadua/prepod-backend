package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "departments")
@Data
@JsonView({ Views.Department.Basic.class, Views.Group.Full.class,
            Views.Institute.Full.class, Views.Professor.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = Department.ALL_JOINS,
                        attributeNodes = {
                                @NamedAttributeNode(value = "institute",
                                                    subgraph = "instituteLite"),
                                @NamedAttributeNode(value = "professors",
                                                    subgraph = "professorLite"),
                                @NamedAttributeNode(value = "groups",
                                                    subgraph = "groupLite")
                        },
                        subgraphs = {
                                @NamedSubgraph(name = "instituteLite", attributeNodes = {}),
                                @NamedSubgraph(name = "professorLite", attributeNodes = {}),
                                @NamedSubgraph(name = "groupLite", attributeNodes = {})
                        }),
                @NamedEntityGraph(name = Department.NO_JOINS)
        }
)
public class Department {

    public static final String ALL_JOINS = "Department.allJoins";
    public static final String NO_JOINS = "Department.noJoins";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "number", nullable = false, unique = true)
    private Integer number;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "institute_id", nullable = false)
    @JsonView(Views.Department.Full.class)
    @EqualsAndHashCode.Exclude
    private Institute institute;

    @OneToMany(mappedBy = "department")
    @JsonView(Views.Department.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Professor> professors;

    @OneToMany(mappedBy = "department")
    @JsonView(Views.Department.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Group> groups;
}
