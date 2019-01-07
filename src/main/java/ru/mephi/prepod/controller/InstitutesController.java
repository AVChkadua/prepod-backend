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
import ru.mephi.prepod.dto.Institute;
import ru.mephi.prepod.repo.InstitutesRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/institutes")
public class InstitutesController {

    private static final String ERROR = "error";
    private static final String INSTITUTE_NOT_FOUND = "Institute with the specified id not found";

    private final InstitutesRepository institutesRepo;

    @Autowired
    public InstitutesController(InstitutesRepository institutesRepo) {
        this.institutesRepo = institutesRepo;
    }

    @GetMapping
    @JsonView(Views.Institute.Basic.class)
    public Iterable<Institute> getAll() {
        return institutesRepo.findAll();
    }

    @GetMapping("/{id}")
    @JsonView(Views.Institute.Full.class)
    public Optional<Institute> getById(@PathVariable("id") String id) {
        return institutesRepo.findById(id);
    }

    @PostMapping
    @JsonView(Views.Institute.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public Institute create(@RequestBody Institute institute) {
        return institutesRepo.save(institute);
    }

    @PutMapping
    @JsonView(Views.Institute.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public ResponseEntity update(@RequestBody Institute institute) {
        if (!institutesRepo.existsById(institute.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, INSTITUTE_NOT_FOUND));
        }
        return ResponseEntity.ok(institutesRepo.save(institute));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(institutesRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}
