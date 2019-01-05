package ru.mephi.prepod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    private Iterable<Bell> getBells() {
        return bellsRepo.findAll();
    }

    @PostMapping
    private Iterable<Bell> save(@RequestBody List<Bell> bells) {
        return bellsRepo.saveAll(bells);
    }

    @PutMapping
    private Iterable<Bell> update(@RequestBody List<Bell> bells) {
        return bellsRepo.saveAll(bells);
    }

    @DeleteMapping
    private void delete(@RequestBody List<Bell> bells) {
        bellsRepo.deleteAll(bells);
    }
}
