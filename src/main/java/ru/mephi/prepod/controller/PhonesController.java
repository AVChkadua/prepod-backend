package ru.mephi.prepod.controller;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.dto.Phone;
import ru.mephi.prepod.repo.PhonesRepository;

import java.util.List;

@RestController
@RequestMapping("/phones")
public class PhonesController {

    private static final String ERROR = "error";
    private static final String PHONE_NOT_FOUND = "Phone not found";

    private final PhonesRepository phonesRepo;

    @Autowired
    public PhonesController(PhonesRepository phonesRepo) {
        this.phonesRepo = phonesRepo;
    }

    @GetMapping
    public Iterable<Phone> getAll() {
        return phonesRepo.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public Phone create(@RequestBody Phone phone) {
        return phonesRepo.save(phone);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public ResponseEntity update(@RequestBody Phone phone) {
        if (!phonesRepo.existsById(phone.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, PHONE_NOT_FOUND));
        }

        return ResponseEntity.ok(phonesRepo.save(phone));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(phonesRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}
