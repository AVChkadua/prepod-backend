package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.dto.Group;
import ru.mephi.prepod.repo.GroupsRepository;
import ru.mephi.prepod.repo.StudentsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
public class GroupsController {

    private static final String GROUP_NOT_FOUND = "Group with the specified id not found";
    private static final String ERROR = "error";
    private final GroupsRepository groupsRepo;
    private final StudentsRepository studentsRepo;

    @Autowired
    public GroupsController(GroupsRepository groupsRepo, StudentsRepository studentsRepo) {
        this.groupsRepo = groupsRepo;
        this.studentsRepo = studentsRepo;
    }

    @GetMapping
    @JsonView(Views.Group.WithParent.class)
    public Iterable<Group> getAll() {
        return groupsRepo.findAll();
    }

    @GetMapping("/parent")
    @JsonView(Views.Group.Basic.class)
    public List<Group> parentOnly() {
        return groupsRepo.findAllByParentGroupIsNull();
    }

    @GetMapping("/{id}")
    @JsonView(Views.Group.Full.class)
    public Optional<Group> getById(@PathVariable("id") String groupId) {
        Optional<Group> group = groupsRepo.findById(groupId);
        if (group.isPresent() && group.get().getStudents().isEmpty()) {
            group.get().setStudents(Sets.newHashSet(studentsRepo.findAllByGroupId(groupId)));
        }
        return group;
    }

    @PostMapping
    @JsonView(Views.Group.WithParent.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public Group create(@RequestBody Group group) {
        return groupsRepo.save(group);
    }

    @PutMapping
    @JsonView(Views.Group.WithParent.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public ResponseEntity update(@RequestBody Group group) {
        if (!groupsRepo.existsById(group.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, GROUP_NOT_FOUND));
        }
        return ResponseEntity.ok(groupsRepo.save(group));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(groupsRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}
