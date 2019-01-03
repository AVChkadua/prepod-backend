package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Professor;

public interface ProfessorsRepository extends CrudRepository<Professor, String> {
}
