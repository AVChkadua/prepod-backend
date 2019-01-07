package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.mephi.prepod.dto.Institute;

import java.util.Optional;

public interface InstitutesRepository extends CrudRepository<Institute, String> {

    @EntityGraph(Institute.ALL_JOINS)
    @Override
    @NonNull
    Optional<Institute> findById(@NonNull String id);

    @EntityGraph(Institute.NO_JOINS)
    @Override
    @NonNull
    Iterable<Institute> findAll();
}
