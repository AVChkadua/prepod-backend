package ru.mephi.prepod.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(name = "roles_privileges")
    @EqualsAndHashCode.Exclude
    private Set<Privilege> privileges;

    @ManyToMany
    @JoinTable(name = "roles_activities")
    @EqualsAndHashCode.Exclude
    private Set<Activity> activities;
}
