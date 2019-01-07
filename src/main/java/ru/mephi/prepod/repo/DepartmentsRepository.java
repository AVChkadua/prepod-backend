package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.mephi.prepod.dto.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentsRepository extends CrudRepository<Department, String> {

    @EntityGraph(Department.NO_JOINS)
    @Override
    @NonNull
    Iterable<Department> findAll();

    @EntityGraph(Department.ALL_JOINS)
    @Override
    @NonNull
    Optional<Department> findById(@NonNull String id);

    @Query("from Department d where d.institute.id = :instituteId")
    @EntityGraph(Department.NO_JOINS)
    List<Department> findAllByInstituteId(@Param("instituteId") String instituteId);
}
