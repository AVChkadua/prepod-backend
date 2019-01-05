package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Location;

public interface LocationsRepository extends CrudRepository<Location, String> {
}
