package ru.mephi.prepod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.dto.Bell;
import ru.mephi.prepod.repo.BellsRepository;

import java.util.List;

@RestController
@RequestMapping("/bells")
public class BellsController {

    private final BellsRepository bellsRepo;

    @Autowired
    public BellsController(BellsRepository bellsRepo) {
        this.bellsRepo = bellsRepo;
    }

    @GetMapping
    private Iterable<Bell> getAll() {
        return bellsRepo.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    private Iterable<Bell> save(@RequestBody List<Bell> bells) {
        return bellsRepo.saveAll(bells);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    private Iterable<Bell> update(@RequestBody List<Bell> bells) {
        return bellsRepo.saveAll(bells);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    private void delete(@RequestBody List<String> ids) {
        ids.forEach(bellsRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}
