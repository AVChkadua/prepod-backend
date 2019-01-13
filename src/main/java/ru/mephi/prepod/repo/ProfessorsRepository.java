package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.mephi.prepod.dto.Professor;
import ru.mephi.prepod.dto.User;

import java.util.Optional;

public interface ProfessorsRepository extends CrudRepository<Professor, String> {

    @Override
    @EntityGraph(Professor.NO_JOINS)
    @NonNull
    Iterable<Professor> findAll();

    @Override
    @EntityGraph(Professor.ALL_JOINS)
    @NonNull
    Optional<Professor> findById(@NonNull String id);

    @EntityGraph(Professor.ALL_JOINS)
    Optional<Professor> findByUser(User user);
}
