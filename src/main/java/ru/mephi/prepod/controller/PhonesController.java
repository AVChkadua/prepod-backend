package ru.mephi.prepod.controller;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.dto.Phone;
import ru.mephi.prepod.repo.PhonesRepository;

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
    public Iterable<Phone> getPhones() {
        return phonesRepo.findAll();
    }

    @PostMapping
    public Phone create(@RequestBody Phone phone) {
        return phonesRepo.save(phone);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody Phone phone) {
        if (!phonesRepo.existsById(phone.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, PHONE_NOT_FOUND));
        }

        return ResponseEntity.ok(phonesRepo.save(phone));
    }
}
