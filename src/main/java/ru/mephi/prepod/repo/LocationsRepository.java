package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.mephi.prepod.dto.Location;

import java.util.Optional;

public interface LocationsRepository extends CrudRepository<Location, String> {

    @EntityGraph(Location.ALL_JOINS)
    @NonNull
    Optional<Location> findById(@NonNull String id);
}
