package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "groups")
@Data
@JsonView({ Views.Group.Basic.class, Views.Department.Full.class,
            Views.Lesson.Full.class, Views.Student.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = Group.ALL_JOINS,
                        attributeNodes = {
                                @NamedAttributeNode(value = "parentGroup",
                                                    subgraph = "parentGroupGraph"),
                                @NamedAttributeNode(value = "department",
                                                    subgraph = "departmentLite"),
                                @NamedAttributeNode(value = "students",
                                                    subgraph = "studentLite"),
                                @NamedAttributeNode(value = "lessons",
                                                    subgraph = "lessonLite")
                        },
                        subgraphs = {
                                @NamedSubgraph(name = "parentGroupGraph", attributeNodes = {
                                        @NamedAttributeNode("department"),
                                        @NamedAttributeNode("students"),
                                        @NamedAttributeNode("lessons")
                                }),
                                @NamedSubgraph(name = "departmentLite", attributeNodes = {}),
                                @NamedSubgraph(name = "studentLite", attributeNodes = {}),
                                @NamedSubgraph(name = "lessonLite", attributeNodes = {})
                        }),
                @NamedEntityGraph(
                        name = Group.WITH_PARENT,
                        attributeNodes = {
                                @NamedAttributeNode(value = "parentGroup",
                                                    subgraph = "groupLite")
                        },
                        subgraphs = {
                                @NamedSubgraph(name = "parentGroupGraph", attributeNodes = {
                                        @NamedAttributeNode("department"),
                                        @NamedAttributeNode("students"),
                                        @NamedAttributeNode("lessons")
                                })
                        }),
                @NamedEntityGraph(name = Group.NO_JOINS)
        }
)
public class Group {

    public static final String ALL_JOINS = "Group.allJoins";
    public static final String WITH_PARENT = "Group.withParent";
    public static final String NO_JOINS = "Group.noJoins";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "number", nullable = false)
    private String number;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonView(Views.Group.WithParent.class)
    @EqualsAndHashCode.Exclude
    private Group parentGroup;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @JsonView(Views.Group.Full.class)
    @EqualsAndHashCode.Exclude
    private Department department;

    @OneToMany(mappedBy = "group")
    @JsonView(Views.Group.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Student> students;

    @ManyToMany(mappedBy = "groups")
    @JsonView(Views.Group.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Lesson> lessons;
}
