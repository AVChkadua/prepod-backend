package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Student;

import java.util.List;

public interface StudentsRepository extends CrudRepository<Student, String> {

    @Query("from Student s where s.group.id = :groupId or (s.group.parentGroup is not null " +
           "and s.group.parentGroup.id = :groupId)")
    List<Student> findAllByGroupId(@Param("groupId") String groupId);
}
