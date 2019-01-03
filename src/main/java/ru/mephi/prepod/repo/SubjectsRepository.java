package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Subject;

import java.util.List;

public interface SubjectsRepository extends CrudRepository<Subject, String> {

    @Query("from Subject s join Lesson l join Professor p where p.id = :professorId")
    List<Subject> findAllByProfessorId(@Param("professorId") String professorId);
}
