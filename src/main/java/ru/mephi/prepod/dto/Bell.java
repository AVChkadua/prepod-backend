package ru.mephi.prepod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.mephi.prepod.common.LocalTimeDeserializer;
import ru.mephi.prepod.common.LocalTimeSerializer;
import ru.mephi.prepod.Views;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "bells")
@Data
@JsonView({ Views.Bell.class, Views.Lesson.Basic.class, Views.Group.Full.class,
            Views.Location.Full.class, Views.Professor.Full.class })
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Bell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "is_opening", nullable = false)
    private Boolean isOpening;

    @Column(name = "time", nullable = false, unique = true)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime time;
}
