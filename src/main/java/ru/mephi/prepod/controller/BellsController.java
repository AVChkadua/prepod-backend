package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.prepod.View;
import ru.mephi.prepod.dto.Bell;
import ru.mephi.prepod.repo.BellsRepository;

@RestController
@RequestMapping("/bells")
public class BellsController {

    private final BellsRepository bellsRepo;

    @Autowired
    public BellsController(BellsRepository bellsRepo) {
        this.bellsRepo = bellsRepo;
    }

    @GetMapping
    @JsonView(View.Summary.class)
    private Iterable<Bell> getBells() {
        return bellsRepo.findAll();
    }
}
