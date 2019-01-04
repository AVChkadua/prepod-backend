package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.prepod.View;
import ru.mephi.prepod.dto.Phone;
import ru.mephi.prepod.repo.PhonesRepository;

@RestController
@RequestMapping("/phones")
public class PhonesController {

    private final PhonesRepository phonesRepo;

    @Autowired
    public PhonesController(PhonesRepository phonesRepo) {
        this.phonesRepo = phonesRepo;
    }

    @GetMapping
    @JsonView(View.Summary.class)
    public Iterable<Phone> getPhones() {
        return phonesRepo.findAll();
    }
}
