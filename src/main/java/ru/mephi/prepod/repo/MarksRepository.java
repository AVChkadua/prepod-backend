package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Mark;

import java.util.List;

public interface MarksRepository extends CrudRepository<Mark, String> {

    @Query("from Mark m where " +
           "(m.student.group.id = :groupId or " +
           "(m.student.group.parentGroup is not null and m.student.group.parentGroup.id = :groupId)) " +
           "and m.subject.id = :subjectId " +
           "and upper(m.name) = upper(:name)")
    @EntityGraph(Mark.ALL_JOINS)
    List<Mark> findAllByNameAndSubjectAndGroupId(@Param("name") String name,
                                                 @Param("subjectId") String subjectId,
                                                 @Param("groupId") String groupId);

    @Query("from Mark m where (m.student.group.id = :groupId or " +
           "(m.student.group.parentGroup is not null and m.student.group.parentGroup.id = :groupId)) " +
           "and m.subject.id = :subjectId")
    @EntityGraph(Mark.ALL_JOINS)
    List<Mark> findAllBySubjectIdAndGroupId(@Param("subjectId") String subjectId,
                                            @Param("groupId") String groupId);
}
