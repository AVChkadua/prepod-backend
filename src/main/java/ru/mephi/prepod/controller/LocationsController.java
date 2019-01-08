package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.dto.Location;
import ru.mephi.prepod.repo.LocationsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/locations")
public class LocationsController {

    private final LocationsRepository locationsRepo;

    @Autowired
    public LocationsController(LocationsRepository locationsRepo) {
        this.locationsRepo = locationsRepo;
    }

    @GetMapping
    @JsonView(Views.Location.Basic.class)
    public Iterable<Location> getAll() {
        return locationsRepo.findAll();
    }

    @GetMapping("/{id}")
    @JsonView(Views.Location.Full.class)
    public Optional<Location> getById(@PathVariable("id") String id) {
        return locationsRepo.findById(id);
    }

    @PostMapping
    @JsonView(Views.Location.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_LOCATIONS)")
    public Location create(@RequestBody Location location) {
        return locationsRepo.save(location);
    }

    @PutMapping
    @JsonView(Views.Location.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_LOCATIONS)")
    public ResponseEntity update(@RequestBody Location location) {
        return ResponseEntity.ok(locationsRepo.save(location));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_LOCATIONS)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(locationsRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}
