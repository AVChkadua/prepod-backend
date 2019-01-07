package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.dto.Student;
import ru.mephi.prepod.repo.GroupsRepository;
import ru.mephi.prepod.repo.StudentsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentsController {

    private static final String ERROR = "error";
    private static final String GROUP_NOT_FOUND = "Group not found";
    private static final String STUDENT_NOT_FOUND = "Student not found";

    private final StudentsRepository studentsRepo;

    private final GroupsRepository groupsRepo;

    @Autowired
    public StudentsController(StudentsRepository studentsRepo, GroupsRepository groupsRepo) {
        this.studentsRepo = studentsRepo;
        this.groupsRepo = groupsRepo;
    }

    @GetMapping("/byGroup")
    @JsonView(Views.Student.Basic.class)
    public List<Student> getStudents(@RequestParam("groupId") String groupId) {
        return studentsRepo.findAllByGroupId(groupId);
    }

    @GetMapping("/{id}")
    @JsonView(Views.Student.Full.class)
    public Optional<Student> getById(@PathVariable("id") String id) {
        return studentsRepo.findById(id);
    }

    @PostMapping
    @JsonView(Views.Student.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public ResponseEntity create(@RequestBody Student student) {
        if (!groupsRepo.existsById(student.getGroup().getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, GROUP_NOT_FOUND));
        }

        return ResponseEntity.ok(studentsRepo.save(student));
    }

    @PutMapping
    @JsonView(Views.Student.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public ResponseEntity update(@RequestBody Student student) {
        if (!studentsRepo.existsById(student.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENT_NOT_FOUND));
        }

        if (!groupsRepo.existsById(student.getGroup().getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, GROUP_NOT_FOUND));
        }

        return ResponseEntity.ok(studentsRepo.save(student));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(studentsRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}
