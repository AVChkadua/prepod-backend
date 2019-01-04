package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Mark;

import java.util.List;

public interface MarksRepository extends CrudRepository<Mark, String> {

    @Query("from Mark m join Student s join Group g join Subject subj " +
           "where g.id = :groupId and subj.id = :subjectId and m.name = upper(:name)")
    List<Mark> findAllByNameAndSubjectAndGroupId(@Param("name") String name,
                                                 @Param("subjectId") String subjectId,
                                                 @Param("groupId") String groupId);

    @Query("from Mark m join Student s join Group g join Subject subj " +
           "where g.id = :groupId and subj.id = :subjectId")
    List<Mark> findAllBySubjectIdAndGroupId(@Param("subjectId") String subjectId,
                                            @Param("groupId") String groupId);
}
