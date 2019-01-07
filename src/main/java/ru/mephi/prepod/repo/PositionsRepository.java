package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.mephi.prepod.dto.Position;

import java.util.Optional;

public interface PositionsRepository extends CrudRepository<Position, String> {

    @Override
    @EntityGraph(Position.ALL_JOINS)
    @NonNull
    Optional<Position> findById(@NonNull String id);

    @Override
    @EntityGraph(Position.NO_JOINS)
    @NonNull
    Iterable<Position> findAll();
}
