package ru.mephi.prepod.controller;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.dto.Subject;
import ru.mephi.prepod.repo.SubjectsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subjects")
public class SubjectsController {

    private static final String ERROR = "error";
    private static final String SUBJECT_NOT_FOUND = "Subject not found";

    private final SubjectsRepository subjectsRepo;

    @Autowired
    public SubjectsController(SubjectsRepository subjectsRepo) {
        this.subjectsRepo = subjectsRepo;
    }

    @GetMapping
    public Iterable<Subject> getAll() {
        return subjectsRepo.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Subject> getById(@PathVariable("id") String id) {
        return subjectsRepo.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_SUBJECTS)")
    public Subject create(@RequestBody Subject subject) {
        return subjectsRepo.save(subject);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_SUBJECTS)")
    public ResponseEntity update(@RequestBody Subject subject) {
        if (!subjectsRepo.existsById(subject.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, SUBJECT_NOT_FOUND));
        }

        return ResponseEntity.ok(subjectsRepo.save(subject));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_SUBJECTS)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(subjectsRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}