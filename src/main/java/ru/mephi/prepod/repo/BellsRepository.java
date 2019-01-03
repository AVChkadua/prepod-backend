package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Bell;

public interface BellsRepository extends CrudRepository<Bell, String> {
}
