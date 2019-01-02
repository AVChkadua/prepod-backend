package ru.mephi.prepod.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(name = "roles_privileges")
    private List<Privilege> privileges;

    @ManyToMany
    @JoinTable(name = "roles_activities")
    private List<Activity> activities;
}
