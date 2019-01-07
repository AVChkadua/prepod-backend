package ru.mephi.prepod.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.mephi.prepod.dto.Group;

import java.util.List;
import java.util.Optional;

public interface GroupsRepository extends CrudRepository<Group, String> {

    @Override
    @EntityGraph(Group.NO_JOINS)
    @NonNull
    Iterable<Group> findAll();

    @Override
    @EntityGraph(Group.ALL_JOINS)
    @NonNull
    Optional<Group> findById(@NonNull String id);

    @EntityGraph(Group.NO_JOINS)
    List<Group> findAllByParentGroupIsNull();

    @Query("from Group g where g.id in :ids or (g.parentGroup is not null and g.parentGroup.id in :ids)")
    @EntityGraph(Group.WITH_PARENT)
    List<Group> findAllByIdOrParentGroupId(@Param("ids") List<String> ids);
}
