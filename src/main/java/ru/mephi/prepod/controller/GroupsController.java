package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.dto.Group;
import ru.mephi.prepod.repo.GroupsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
public class GroupsController {

    public static final String GROUP_NOT_FOUND = "Group with the specified id not found";
    public static final String ERROR = "error";
    private final GroupsRepository groupsRepo;

    @Autowired
    public GroupsController(GroupsRepository groupsRepo) {
        this.groupsRepo = groupsRepo;
    }

    @GetMapping
    public List<Group> getAll() {
        return groupsRepo.findAllByParentGroupIsNull();
    }

    @GetMapping("/{id}")
    @JsonView(Views.Group.Full.class)
    public Optional<Group> getById(@PathVariable("id") String groupId) {
        return groupsRepo.findById(groupId);
    }

    @PostMapping
    @JsonView(Views.Group.WithParent.class)
    public Group create(@RequestBody Group group) {
        return groupsRepo.save(group);
    }

    @PutMapping
    @JsonView(Views.Group.WithParent.class)
    public ResponseEntity update(@RequestBody Group group) {
        if (!groupsRepo.existsById(group.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, GROUP_NOT_FOUND));
        }
        return ResponseEntity.ok(groupsRepo.save(group));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        groupsRepo.deleteById(id);
    }
}
