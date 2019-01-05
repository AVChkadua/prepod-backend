package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Professor;

import java.util.List;

public interface ProfessorsRepository extends CrudRepository<Professor, String> {
}
