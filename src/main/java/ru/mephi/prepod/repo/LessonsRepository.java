package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Lesson;

import java.time.LocalDate;
import java.util.List;

public interface LessonsRepository extends CrudRepository<Lesson, String> {

    @Query("from Lesson l join Group g where g.id = :groupId")
    List<Lesson> findAllByGroupId(@Param("groupId") String groupId);

    @Query("from Lesson l join Professor p where p.id = :professorId")
    List<Lesson> findAllByProfessorId(@Param("professorId") String professorId);

    @Query("from Lesson l join Group g where l.subject.id = :subjectId and g.id = :groupId " +
           "and ((l.startInSemester is null and l.endInSemester is null) or " +
           "(:date between l.startInSemester and l.endInSemester)) ")
    List<Lesson> findBySubjectIdAndDateAndGroupId(@Param("subjectId") String subjectId, @Param("date") LocalDate date,
                                                  @Param("groupId") String groupId);
}
