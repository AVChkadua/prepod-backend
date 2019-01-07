package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.dto.*;
import ru.mephi.prepod.repo.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/timetable")
public class LessonsController {

    private static final String ERROR = "error";
    private static final String LESSON_NOT_FOUND = "Lesson not found";
    private static final String PROFESSOR_NOT_FOUND = "Professor not found";
    private static final String BELL_NOT_FOUND = "Bell not found";
    private static final String GROUP_NOT_FOUND = "Group not found";
    private static final String LOCATION_NOT_FOUND = "Location not found";
    private static final String SUBJECT_NOT_FOUND = "Subject not found";
    private static final String BELL_MISUSE = "Opening bell used for end time or vice versa";

    private final LessonsRepository lessonsRepo;

    private final ProfessorsRepository professorsRepo;

    private final GroupsRepository groupsRepo;

    private final BellsRepository bellsRepository;

    private final LocationsRepository locationsRepo;

    private final SubjectsRepository subjectsRepo;

    @Autowired
    public LessonsController(LessonsRepository lessonsRepo, ProfessorsRepository professorsRepo,
                             GroupsRepository groupsRepo, BellsRepository bellsRepository,
                             LocationsRepository locationsRepo, SubjectsRepository subjectsRepo) {
        this.lessonsRepo = lessonsRepo;
        this.professorsRepo = professorsRepo;
        this.groupsRepo = groupsRepo;
        this.bellsRepository = bellsRepository;
        this.locationsRepo = locationsRepo;
        this.subjectsRepo = subjectsRepo;
    }

    @GetMapping("/group/{id}")
    @JsonView(Views.Lesson.Full.class)
    public Iterable<Lesson> getByGroupId(@PathVariable("id") String groupId) {
        return lessonsRepo.findAllByGroupId(groupId);
    }

    @GetMapping("/professor/{id}")
    @JsonView(Views.Lesson.Full.class)
    public Iterable<Lesson> getByProfessorId(@PathVariable("id") String professorId) {
        return lessonsRepo.findAllByProfessorId(professorId);
    }

    @PostMapping
    @JsonView(Views.Lesson.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public ResponseEntity create(@RequestBody List<Lesson> lessons) {
        List<Professor> professors = lessons.stream()
                .map(Lesson::getProfessors)
                .flatMap(Set::stream)
                .collect(Collectors.toList());
        if (professors.stream().map(Professor::getId).anyMatch(id -> !professorsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, PROFESSOR_NOT_FOUND));
        }

        List<String> startIds = lessons.stream()
                .map(Lesson::getStartBell)
                .map(Bell::getId)
                .distinct()
                .collect(Collectors.toList());
        List<String> endIds = lessons.stream()
                .map(Lesson::getEndBell)
                .map(Bell::getId)
                .distinct()
                .collect(Collectors.toList());
        List<Bell> startBells = Lists.newArrayList(bellsRepository.findAllById(startIds));
        List<Bell> endBells = Lists.newArrayList(bellsRepository.findAllById(endIds));

        if (startBells.size() != startIds.size() || endBells.size() != endIds.size()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, BELL_NOT_FOUND));
        } else if (startBells.stream().anyMatch(b -> !b.getIsOpening())
                   || endBells.stream().anyMatch(Bell::getIsOpening)) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, BELL_MISUSE));
        }

        if (lessons.stream()
                .map(Lesson::getGroups)
                .flatMap(Set::stream)
                .map(Group::getId)
                .anyMatch(id -> !groupsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, GROUP_NOT_FOUND));
        }

        if (lessons.stream()
                .map(Lesson::getLocation)
                .map(Location::getId)
                .anyMatch(id -> !locationsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, LOCATION_NOT_FOUND));
        }

        if (lessons.stream()
                .map(Lesson::getSubject)
                .map(Subject::getId)
                .anyMatch(id -> !subjectsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, SUBJECT_NOT_FOUND));
        }

        return ResponseEntity.ok(lessonsRepo.saveAll(lessons));
    }

    @PutMapping
    @JsonView(Views.Lesson.Full.class)
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public ResponseEntity update(@RequestBody List<Lesson> lessons) {
        if (lessons.stream().map(Lesson::getId).anyMatch(id -> !lessonsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, LESSON_NOT_FOUND));
        }

        List<Professor> professors = lessons.stream()
                .map(Lesson::getProfessors)
                .flatMap(Set::stream)
                .collect(Collectors.toList());
        if (professors.stream().map(Professor::getId).anyMatch(id -> !professorsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, PROFESSOR_NOT_FOUND));
        }

        List<String> startIds = lessons.stream()
                .map(Lesson::getStartBell)
                .map(Bell::getId)
                .distinct()
                .collect(Collectors.toList());
        List<String> endIds = lessons.stream()
                .map(Lesson::getEndBell)
                .map(Bell::getId)
                .distinct()
                .collect(Collectors.toList());
        List<Bell> startBells = Lists.newArrayList(bellsRepository.findAllById(startIds));
        List<Bell> endBells = Lists.newArrayList(bellsRepository.findAllById(endIds));

        if (startBells.size() != startIds.size() || endBells.size() != endIds.size()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, BELL_NOT_FOUND));
        } else if (startBells.stream().anyMatch(b -> !b.getIsOpening())
                   || endBells.stream().anyMatch(Bell::getIsOpening)) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, BELL_MISUSE));
        }

        if (lessons.stream()
                .map(Lesson::getGroups)
                .flatMap(Set::stream)
                .map(Group::getId)
                .anyMatch(id -> !groupsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, GROUP_NOT_FOUND));
        }

        if (lessons.stream()
                .map(Lesson::getLocation)
                .map(Location::getId)
                .anyMatch(id -> !locationsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, LOCATION_NOT_FOUND));
        }

        if (lessons.stream()
                .map(Lesson::getSubject)
                .map(Subject::getId)
                .anyMatch(id -> !subjectsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, SUBJECT_NOT_FOUND));
        }

        return ResponseEntity.ok(lessonsRepo.saveAll(lessons));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT)")
    public void delete(List<String> ids) {
        ids.forEach(lessonsRepo::deleteById);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }

}
