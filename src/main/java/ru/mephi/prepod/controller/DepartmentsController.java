package ru.mephi.prepod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.prepod.dto.Department;
import ru.mephi.prepod.repo.DepartmentsRepository;

import java.util.List;

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
}
