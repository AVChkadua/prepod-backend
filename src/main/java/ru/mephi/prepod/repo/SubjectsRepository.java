package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Subject;

import java.util.List;

public interface SubjectsRepository extends CrudRepository<Subject, String> {

    @Query(value = "SELECT DISTINCT s.* FROM subjects s " +
                   "JOIN lessons l on s.id = l.subject_id " +
                   "JOIN professors_lessons pl ON l.id = pl.lesson_id " +
                   "JOIN professors p ON pl.professor_id = p.id " +
                   "WHERE p.id = :professorId",
           nativeQuery = true)
    List<Subject> findAllByProfessorId(@Param("professorId") String professorId);
}
