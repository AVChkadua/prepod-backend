package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Lesson;

import java.time.LocalDate;
import java.util.List;

public interface LessonsRepository extends CrudRepository<Lesson, String> {

    @Query(value = "SELECT l.* FROM lessons l " +
                   "JOIN groups_lessons gl ON gl.lesson_id = l.id " +
                   "JOIN groups g ON gl.group_id = g.id " +
                   "WHERE g.id = :groupId OR (g.parent_id IS NOT NULL AND g.parent_id = :groupId)",
           nativeQuery = true)
    List<Lesson> findAllByGroupId(@Param("groupId") String groupId);

    @Query(value = "SELECT l.* FROM lessons l " +
                   "JOIN professors_lessons pl on l.id = pl.lesson_id " +
                   "JOIN professors p ON pl.professor_id = p.id " +
                   "WHERE p.id = :professorId",
           nativeQuery = true)
    List<Lesson> findAllByProfessorId(@Param("professorId") String professorId);

    @Query(value = "SELECT * FROM lessons l " +
                   "JOIN subjects s on l.subject_id = s.id " +
                   "JOIN groups_lessons gl on l.id = gl.lesson_id " +
                   "JOIN groups g on gl.group_id = g.id " +
                   "WHERE s.id = :subjectId AND (g.id = :groupId OR g.parent_id = :groupId) " +
                   "AND ((l.start_in_semester is null and l.end_in_semester is null) or " +
                   "(:date between l.start_in_semester and l.end_in_semester)) ",
           nativeQuery = true)
    List<Lesson> findBySubjectIdAndDateAndGroupId(@Param("subjectId") String subjectId, @Param("date") LocalDate date,
                                                  @Param("groupId") String groupId);
}
