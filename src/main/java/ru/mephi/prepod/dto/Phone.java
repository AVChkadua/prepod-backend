package ru.mephi.prepod.dto;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "phones")
@Data
public class Phone {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "additional", nullable = false)
    private String additional;

    @Column(name = "location", nullable = false)
    private String location;
}
