package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends CrudRepository<Attendance, String> {

    @Query("from Attendance a join Student s join Group g " +
           "where g.id = :groupId and a.date = :date and a.lesson.startBell.time = :startTime " +
           "and a.lesson.endBell.time = :endTime")
    List<Attendance> findAllByDateAndSubjectIdAndGroupId(@Param("date") LocalDate date,
                                                         @Param("startTime") String startTime,
                                                         @Param("endTime") String endTime,
                                                         @Param("groupId") String groupId);

    @Query("from Attendance a join Student s join Group g join Subject subj " +
           "where g.id = :groupId and subj.id = :subjectId")
    List<Attendance> findAllBySubjectIdAndGroupId(@Param("subjectId") String subjectId,
                                                  @Param("groupId") String groupId);
}
