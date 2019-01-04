package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import ru.mephi.prepod.View;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "phones")
@Data
public class Phone {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    @JsonView(View.Summary.class)
    private String name;

    @Column(name = "phone", nullable = false)
    @JsonView(View.Summary.class)
    private String phone;

    @Column(name = "additional", nullable = false)
    @JsonView(View.Summary.class)
    private String additional;

    @Column(name = "location", nullable = false)
    @JsonView(View.Summary.class)
    private String location;
}
