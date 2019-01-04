package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.dto.Department;
import ru.mephi.prepod.repo.DepartmentsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
public class DepartmentsController {

    private final DepartmentsRepository departmentsRepo;

    @Autowired
    public DepartmentsController(DepartmentsRepository departmentsRepo) {
        this.departmentsRepo = departmentsRepo;
    }

    @GetMapping("/all")
    public Iterable<Department> getAll() {
        return departmentsRepo.findAll();
    }

    @GetMapping("/byInstitute")
    public List<Department> findByInstitute(@RequestParam("insituteId") String instituteId) {
        return departmentsRepo.findAllByInstituteId(instituteId);
    }

    @GetMapping("/{id}")
    @JsonView(Views.Department.Full.class)
    public Optional<Department> getById(@PathVariable("id") String id) {
        return departmentsRepo.findById(id);
    }
}
