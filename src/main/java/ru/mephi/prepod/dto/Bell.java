package ru.mephi.prepod.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "bells")
@Data
public class Bell {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "is_opening", nullable = false)
    private Boolean isOpening;

    @Column(name = "time", nullable = false)
    private LocalTime time;
}
