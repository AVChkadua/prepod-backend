package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Role;

public interface RolesRepository extends CrudRepository<Role, String> {
}
