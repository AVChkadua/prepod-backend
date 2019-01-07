package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "institutes")
@Data
@JsonView({ Views.Institute.Basic.class, Views.Department.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NamedEntityGraphs(
        {
                @NamedEntityGraph(
                        name = Institute.ALL_JOINS,
                        attributeNodes = @NamedAttributeNode(value = "departments",
                                                             subgraph = "departmentLite"),
                        subgraphs = @NamedSubgraph(name = "departmentLite", attributeNodes = {})),
                @NamedEntityGraph(name = Institute.NO_JOINS)
        }
)
public class Institute {

    public static final String ALL_JOINS = "Institute.allJoins";
    public static final String NO_JOINS = "Institute.noJoins";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "short_name", nullable = false)
    private String short_name;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "institute")
    @JsonView(Views.Institute.Full.class)
    @EqualsAndHashCode.Exclude
    private Set<Department> departments;
}
