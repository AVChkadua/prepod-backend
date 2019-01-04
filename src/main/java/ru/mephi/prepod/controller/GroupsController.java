package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.dto.Group;
import ru.mephi.prepod.dto.Student;
import ru.mephi.prepod.repo.GroupsRepository;
import ru.mephi.prepod.repo.StudentsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
public class GroupsController {

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
}
