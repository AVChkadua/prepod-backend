package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Mark;
import ru.mephi.prepod.dto.Student;
import ru.mephi.prepod.dto.Subject;

import java.time.LocalDate;
import java.util.List;

public interface MarksRepository extends CrudRepository<Mark, String> {

    List<Mark> findAllByDateAndSubjectAndStudent(LocalDate date, Subject subject, Student student);
}
