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
import ru.mephi.prepod.dto.Position;
import ru.mephi.prepod.repo.PositionsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/positions")
public class PositionsController {

    private static final String POSITION_NOT_FOUND = "Position not found";
    private static final String ERROR = "error";
    private final PositionsRepository positionsRepo;

    @Autowired
    public PositionsController(PositionsRepository positionsRepo) {
        this.positionsRepo = positionsRepo;
    }

    @GetMapping
    @JsonView(Views.Position.Basic.class)
    public Iterable<Position> getAll() {
        return positionsRepo.findAll();
    }

    @GetMapping("/{id}")
    @JsonView(Views.Position.Full.class)
    public Optional<Position> getById(@PathVariable("id") String id) {
        return positionsRepo.findById(id);
    }

    @PostMapping
    @JsonView(Views.Position.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_POSITIONS)")
    public Iterable<Position> create(@RequestBody List<Position> positions) {
        return positionsRepo.saveAll(positions);
    }

    @PutMapping
    @JsonView(Views.Position.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_POSITIONS)")
    public ResponseEntity update(@RequestBody List<Position> positions) {
        if (positions.stream().map(Position::getId).anyMatch(id -> !positionsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, POSITION_NOT_FOUND));
        }
        return ResponseEntity.ok(positionsRepo.saveAll(positions));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_POSITIONS)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(positionsRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}
