package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "institutes")
@Data
public class Institute {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "institute")
    @JsonView(Views.Institute.Full.class)
    private List<Department> departments;
}
