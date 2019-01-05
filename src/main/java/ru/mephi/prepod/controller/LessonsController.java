package ru.mephi.prepod.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.Views;
import ru.mephi.prepod.dto.*;
import ru.mephi.prepod.repo.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/timetable")
public class LessonsController {

    private static final String ERROR = "error";
    private static final String LESSON_NOT_FOUND = "Lesson not found";
    private static final String PROFESSOR_NOT_FOUND = "Professor not found";
    private static final String BELL_NOT_FOUND = "Bell for the specified time not found";
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
    public Iterable<Lesson> getGroupTimetable(@PathVariable("id") String groupId) {
        return lessonsRepo.findAllByGroupId(groupId);
    }

    @GetMapping("/professor/{id}")
    @JsonView(Views.Lesson.Full.class)
    public Iterable<Lesson> getProfessorTimetable(@PathVariable("id") String professorId) {
        return lessonsRepo.findAllByProfessorId(professorId);
    }

    @PostMapping
    @JsonView(Views.Lesson.Full.class)
    public ResponseEntity create(@RequestBody List<Lesson> lessons) {
        List<Professor> professors = lessons.stream()
                .map(Lesson::getProfessors)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (professors.stream().map(Professor::getId).anyMatch(id -> !professorsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, PROFESSOR_NOT_FOUND));
        }

        List<LocalTime> startTimes = lessons.stream()
                .map(Lesson::getStartBell)
                .map(Bell::getTime)
                .collect(Collectors.toList());
        List<LocalTime> endTimes = lessons.stream()
                .map(Lesson::getEndBell)
                .map(Bell::getTime)
                .collect(Collectors.toList());
        Map<LocalTime, Bell> startBells = getBells(startTimes);
        Map<LocalTime, Bell> endBells = getBells(endTimes);

        if (startBells.size() != startTimes.size() || endBells.size() != endTimes.size()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, BELL_NOT_FOUND));
        } else if (startBells.values().stream().anyMatch(b -> !b.getIsOpening())
                   || endBells.values().stream().anyMatch(Bell::getIsOpening)) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, BELL_MISUSE));
        }

        if (lessons.stream()
                .map(Lesson::getGroups)
                .flatMap(List::stream)
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
    public ResponseEntity update(@RequestBody List<Lesson> lessons) {
        if (lessons.stream().map(Lesson::getId).anyMatch(id -> !lessonsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, LESSON_NOT_FOUND));
        }

        List<Professor> professors = lessons.stream()
                .map(Lesson::getProfessors)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (professors.stream().map(Professor::getId).anyMatch(id -> !professorsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, PROFESSOR_NOT_FOUND));
        }

        List<LocalTime> startTimes = lessons.stream()
                .map(Lesson::getStartBell)
                .map(Bell::getTime)
                .collect(Collectors.toList());
        List<LocalTime> endTimes = lessons.stream()
                .map(Lesson::getEndBell)
                .map(Bell::getTime)
                .collect(Collectors.toList());
        Map<LocalTime, Bell> startBells = getBells(startTimes);
        Map<LocalTime, Bell> endBells = getBells(endTimes);

        if (startBells.size() != startTimes.size() || endBells.size() != endTimes.size()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, BELL_NOT_FOUND));
        } else if (startBells.values().stream().anyMatch(b -> !b.getIsOpening())
                   || endBells.values().stream().anyMatch(Bell::getIsOpening)) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, BELL_MISUSE));
        }

        if (lessons.stream()
                .map(Lesson::getGroups)
                .flatMap(List::stream)
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
    public void delete(List<String> ids) {
        ids.forEach(lessonsRepo::deleteById);
    }

    private Map<LocalTime, Bell> getBells(List<LocalTime> times) {
        return bellsRepository.findAllByTimeIn(times).stream()
                .collect(Collectors.toMap(Bell::getTime, Function.identity()));
    }
}
