package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.mephi.prepod.dto.Department;

import java.util.List;

public interface DepartmentsRepository extends CrudRepository<Department, String> {

    @Query("from Department d join Institute i where i.id = :instituteId")
    List<Department> findAllByInstituteId(@Param("instituteId") String instituteId);
}
