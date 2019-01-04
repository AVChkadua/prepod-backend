package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.View;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "institutes")
@Data
public class Institute {

    @Id
    @Column(name = "id", nullable = false)
    @JsonView(View.Summary.class)
    private String id;

    @Column(name = "name", nullable = false)
    @JsonView(View.Summary.class)
    private String name;

    @OneToMany(mappedBy = "institute")
    private List<Department> departments;
}
