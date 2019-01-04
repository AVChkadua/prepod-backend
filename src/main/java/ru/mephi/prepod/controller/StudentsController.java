package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.dto.Student;
import ru.mephi.prepod.repo.StudentsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentsController {

    private final StudentsRepository studentsRepo;

    @Autowired
    public StudentsController(StudentsRepository studentsRepo) {
        this.studentsRepo = studentsRepo;
    }

    @GetMapping("/byGroup")
    public List<Student> getStudents(@RequestParam("groupId") String groupId) {
        return studentsRepo.findAllByGroupId(groupId);
    }

    @GetMapping("/{id}")
    @JsonView(Views.Student.Full.class)
    public Optional<Student> findById(@PathVariable("id") String id) {
        return studentsRepo.findById(id);
    }
}
