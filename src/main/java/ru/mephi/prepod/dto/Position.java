package ru.mephi.prepod.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "positions")
@Data
public class Position {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "positions")
    private List<Professor> professors;
}
