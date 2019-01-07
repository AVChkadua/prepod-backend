package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "positions")
@Data
@JsonView({ Views.Position.Basic.class, Views.Professor.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = Position.ALL_JOINS,
                        attributeNodes = @NamedAttributeNode(value = "professors",
                                                             subgraph = "professorLite"),
                        subgraphs = @NamedSubgraph(name = "professorsLite", attributeNodes = {})),
                @NamedEntityGraph(name = Position.NO_JOINS)
        }
)
public class Position {

    public static final String ALL_JOINS = "Position.allJoins";
    public static final String NO_JOINS = "Position.noJoins";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "positions")
    @JsonView(Views.Position.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Professor> professors;
}
