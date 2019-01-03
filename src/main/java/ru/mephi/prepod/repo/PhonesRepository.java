package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Phone;

public interface PhonesRepository extends CrudRepository<Phone, String> {
}
