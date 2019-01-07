package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "locations")
@Data
@JsonView({ Views.Location.Basic.class, Views.Lesson.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = Location.ALL_JOINS,
                        attributeNodes = {
                                @NamedAttributeNode(value = "lessons",
                                                    subgraph = "lessonLite")
                        },
                        subgraphs = @NamedSubgraph(name = "lessonLite", attributeNodes = {})),
                @NamedEntityGraph(name = Location.No_JOINS)
        }
)
public class Location {

    public static final String ALL_JOINS = "Location.allJoins";
    public static final String No_JOINS = "Location.noJoins";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "location")
    @JsonView(Views.Location.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Lesson> lessons;
}
