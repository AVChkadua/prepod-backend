package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends CrudRepository<Attendance, String> {

    @Query("from Attendance a where a.student.group.id = :groupId and a.date = :date and a.lesson.id = :lessonId")
    @EntityGraph(Attendance.ALL_JOINS)
    List<Attendance> findAllByDateAndLessonIdAndGroupId(@Param("date") LocalDate date,
                                                        @Param("lessonId") String lessonId,
                                                        @Param("groupId") String groupId);

    @Query("from Attendance a where a.student.group.id = :groupId and a.lesson.id = :lessonId")
    @EntityGraph(Attendance.ALL_JOINS)
    List<Attendance> findAllByLessonIdAndGroupId(@Param("lessonId") String lessonId,
                                                 @Param("groupId") String groupId);
}
