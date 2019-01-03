package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.User;

public interface UsersRepository extends CrudRepository<User, String> {
}
