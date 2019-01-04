package ru.mephi.prepod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    private final StudentsRepository studentsRepo;

    @Autowired
    public GroupsController(GroupsRepository groupsRepo, StudentsRepository studentsRepo) {
        this.groupsRepo = groupsRepo;
        this.studentsRepo = studentsRepo;
    }

    @GetMapping
    public List<Group> getAll() {
        return groupsRepo.findAllByParentGroupIsNull();
    }

    @GetMapping("/{id}/students")
    public List<Student> getStudents(@PathVariable("id") String groupId) {
        return studentsRepo.findAllByGroupId(groupId);
    }

    @GetMapping("/{id}")
    public Optional<Group> getById(@PathVariable("id") String groupId) {
        return groupsRepo.findById(groupId);
    }
}
