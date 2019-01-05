package ru.mephi.prepod.dto;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "privileges")
@Data
public class Privilege {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;
}
