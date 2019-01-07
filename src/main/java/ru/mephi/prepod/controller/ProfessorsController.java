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
import ru.mephi.prepod.dto.Professor;
import ru.mephi.prepod.dto.Subject;
import ru.mephi.prepod.repo.DepartmentsRepository;
import ru.mephi.prepod.repo.PositionsRepository;
import ru.mephi.prepod.repo.ProfessorsRepository;
import ru.mephi.prepod.repo.SubjectsRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/professors")
public class ProfessorsController {

    private static final String ERROR = "error";
    private static final String DEPARTMENT_NOT_FOUND = "Department not found";
    private static final String POSITION_NOT_FOUND = "Position not found";
    private static final String PROFESSOR_NOT_FOUND = "Professor not found";

    private final ProfessorsRepository professorsRepo;
    private final SubjectsRepository subjectsRepo;
    private final DepartmentsRepository departmentsRepo;
    private final PositionsRepository positionsRepository;

    @Autowired
    public ProfessorsController(ProfessorsRepository professorsRepo, SubjectsRepository subjectsRepo,
                                DepartmentsRepository departmentsRepo,
                                PositionsRepository positionsRepository) {
        this.professorsRepo = professorsRepo;
        this.subjectsRepo = subjectsRepo;
        this.departmentsRepo = departmentsRepo;
        this.positionsRepository = positionsRepository;
    }

    @GetMapping
    @JsonView(Views.Professor.Basic.class)
    public Iterable<Professor> getAll() {
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

    @PostMapping
    @JsonView(Views.Professor.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public ResponseEntity create(@RequestBody Professor professor) {
        if (!departmentsRepo.existsById(professor.getDepartment().getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, DEPARTMENT_NOT_FOUND));
        }

        if (professor.getPositions().stream()
                .map(Position::getId)
                .anyMatch(id -> !positionsRepository.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, POSITION_NOT_FOUND));
        }

        return ResponseEntity.ok(professorsRepo.save(professor));
    }

    @PutMapping
    @JsonView(Views.Professor.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public ResponseEntity update(@RequestBody Professor professor) {
        if (!professorsRepo.existsById(professor.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, PROFESSOR_NOT_FOUND));
        }

        if (!departmentsRepo.existsById(professor.getDepartment().getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, DEPARTMENT_NOT_FOUND));
        }

        return ResponseEntity.ok(professorsRepo.save(professor));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(professorsRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }
}
