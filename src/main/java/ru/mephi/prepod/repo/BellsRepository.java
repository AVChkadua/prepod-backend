package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Bell;

import java.time.LocalTime;
import java.util.List;

public interface BellsRepository extends CrudRepository<Bell, String> {
    List<Bell> findAllByTimeIn(List<LocalTime> times);
}
