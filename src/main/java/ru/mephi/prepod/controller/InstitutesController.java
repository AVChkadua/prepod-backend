package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.dto.Institute;
import ru.mephi.prepod.repo.InstitutesRepository;

import java.util.Optional;

@RestController
@RequestMapping("/institutes")
public class InstitutesController {

    private final InstitutesRepository institutesRepo;

    @Autowired
    public InstitutesController(InstitutesRepository institutesRepo) {
        this.institutesRepo = institutesRepo;
    }

    @GetMapping("/list")
    public Iterable<Institute> findAll() {
        return institutesRepo.findAll();
    }

    @GetMapping("/{id}")
    @JsonView(Views.Institute.Full.class)
    public Optional<Institute> findById(@PathVariable("id") String id) {
        return institutesRepo.findById(id);
    }
}
