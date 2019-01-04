package ru.mephi.prepod.repo;

import org.springframework.data.repository.CrudRepository;
import ru.mephi.prepod.dto.Group;

import java.util.List;

public interface GroupsRepository extends CrudRepository<Group, String> {

    List<Group> findAllByParentGroupIsNull();
}
