package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Institute;

public interface InstitutesRepository extends CrudRepository<Institute, String> {
}
