package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.dto.Department;
import ru.mephi.prepod.repo.DepartmentsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
public class DepartmentsController {

    private static final String ERROR = "error";
    private static final String DEPARTMENT_NOT_FOUND = "The department with the specified id is not found";

    private final DepartmentsRepository departmentsRepo;

    @Autowired
    public DepartmentsController(DepartmentsRepository departmentsRepo) {
        this.departmentsRepo = departmentsRepo;
    }

    @GetMapping
    @JsonView(Views.Department.Basic.class)
    public Iterable<Department> getAll() {
        return departmentsRepo.findAll();
    }

    @GetMapping("/byInstitute")
    @JsonView(Views.Department.Basic.class)
    public List<Department> getByInstitute(@RequestParam("instituteId") String instituteId) {
        return departmentsRepo.findAllByInstituteId(instituteId);
    }

    @GetMapping("/{id}")
    @JsonView(Views.Department.Full.class)
    public Optional<Department> getById(@PathVariable("id") String id) {
        return departmentsRepo.findById(id);
    }

    @PostMapping
    @JsonView(Views.Department.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public Department create(@RequestBody Department department) {
        return departmentsRepo.save(department);
    }

    @PutMapping
    @JsonView(Views.Department.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public ResponseEntity update(@RequestBody Department department) {
        if (!departmentsRepo.existsById(department.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, DEPARTMENT_NOT_FOUND));
        }
        return ResponseEntity.ok(departmentsRepo.save(department));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(departmentsRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}
