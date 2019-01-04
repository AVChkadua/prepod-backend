package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.dto.Professor;
import ru.mephi.prepod.dto.Subject;
import ru.mephi.prepod.repo.ProfessorsRepository;
import ru.mephi.prepod.repo.SubjectsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/professors")
public class ProfessorsController {

    private final ProfessorsRepository professorsRepo;

    private final SubjectsRepository subjectsRepo;

    @Autowired
    public ProfessorsController(ProfessorsRepository professorsRepo, SubjectsRepository subjectsRepo) {
        this.professorsRepo = professorsRepo;
        this.subjectsRepo = subjectsRepo;
    }

    @GetMapping
    public Iterable<Professor> getProfessorsList() {
        return professorsRepo.findAll();
    }

    @GetMapping("/{id}/subjects")
    public List<Subject> getProfessorSubjects(@PathVariable("id") String professorId) {
        return subjectsRepo.findAllByProfessorId(professorId);
    }

    @GetMapping("/{id}")
    @JsonView(Views.Professor.Full.class)
    public Optional<Professor> getById(@PathVariable("id") String id) {
        return professorsRepo.findById(id);
    }
}
