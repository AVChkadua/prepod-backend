package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Attendance;
import ru.mephi.prepod.dto.Student;
import ru.mephi.prepod.dto.Subject;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends CrudRepository<Attendance, String> {

    List<Attendance> findAllByDateAndSubjectAndStudent(LocalDate date, Subject subject, Student student);
}
