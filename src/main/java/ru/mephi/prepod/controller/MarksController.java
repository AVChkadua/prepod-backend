package ru.mephi.prepod.controller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.common.LocalDateDeserializer;
import ru.mephi.prepod.common.LocalDateSerializer;
import ru.mephi.prepod.dto.Group;
import ru.mephi.prepod.dto.Mark;
import ru.mephi.prepod.dto.Student;
import ru.mephi.prepod.dto.Subject;
import ru.mephi.prepod.repo.GroupsRepository;
import ru.mephi.prepod.repo.MarksRepository;
import ru.mephi.prepod.repo.StudentsRepository;
import ru.mephi.prepod.repo.SubjectsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/marks")
public class MarksController {

    private static final String ERROR = "error";
    private static final String GROUP_NOT_FOUND = "Group not found";
    private static final String MARK_NOT_FOUND = "Mark not found";
    private static final String SUBJECT_NOT_FOUND = "Subject not found";
    private static final String STUDENT_NOT_FOUND = "Student not found";
    private static final String STUDENT_IS_EXPELLED = "Student is expelled";
    private static final String STUDENT_IS_ON_A_BREAK = "Student is on a break";
    private static final String STUDENT_GROUP_MISMATCH = "Student doesn't belong to the specified group";

    private final MarksRepository marksRepo;

    private final SubjectsRepository subjectsRepo;

    private final StudentsRepository studentsRepo;

    private final GroupsRepository groupsRepo;

    @Autowired
    public MarksController(MarksRepository marksRepo, SubjectsRepository subjectsRepo,
                           StudentsRepository studentsRepo, GroupsRepository groupsRepo) {
        this.marksRepo = marksRepo;
        this.subjectsRepo = subjectsRepo;
        this.studentsRepo = studentsRepo;
        this.groupsRepo = groupsRepo;
    }

    @GetMapping("/byNameAndSubjectAndGroup")
    public Marks getByNameAndSubjectAndGroup(@RequestParam("name") String name,
                                             @RequestParam("subjectId") String subjectId,
                                             @RequestParam("groupId") String groupId) {
        List<Mark> all = marksRepo.findAllByNameAndSubjectAndGroupId(name, subjectId, groupId);
        Map<String, Integer> marks = all.stream()
                .collect(Collectors.toMap(m -> m.getStudent().getId(), m -> Integer.valueOf(m.getMark())));
        return new Marks(all.get(0).getName(),
                all.get(0).getStudent().getGroup().getId(),
                all.get(0).getSubject().getId(),
                all.get(0).getDate(),
                marks);
    }

    @GetMapping("/bySubjectAndGroup")
    public List<Marks> getBySubjectAndGroup(@RequestParam("subjectId") String subjectId,
                                            @RequestParam("groupId") String groupId) {
        List<Marks> list = new ArrayList<>();
        List<Mark> all = marksRepo.findAllBySubjectIdAndGroupId(subjectId, groupId);
        Map<String, List<Mark>> byName = all.stream().collect(Collectors.groupingBy(Mark::getName));
        for (List<Mark> marks : byName.values()) {
            Map<String, Integer> byStudentId = marks.stream()
                    .collect(Collectors.toMap(m -> m.getStudent().getId(), m -> Integer.valueOf(m.getMark())));
            list.add(new Marks(marks.get(0).getName(),
                    all.get(0).getStudent().getGroup().getId(),
                    all.get(0).getSubject().getId(),
                    marks.get(0).getDate(), byStudentId));
        }
        return list;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT, " +
                  "T(ru.mephi.prepod.security.Role).PROFESSOR)")
    public ResponseEntity create(@RequestBody Marks marks) {
        Optional<Group> group = groupsRepo.findById(marks.getGroupId());
        if (!group.isPresent()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, GROUP_NOT_FOUND));
        }

        Optional<Subject> subject = subjectsRepo.findById(marks.getSubjectId());
        if (!subject.isPresent()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, SUBJECT_NOT_FOUND));
        }

        if (marks.getMarks().keySet().stream().anyMatch(id -> !studentsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENT_NOT_FOUND));
        }

        Map<String, Student> students = studentsRepo.findAllByGroupId(marks.getGroupId()).stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));
        if (students.size() < marks.getMarks().keySet().size()
            || !students.keySet().containsAll(marks.getMarks().keySet())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENT_GROUP_MISMATCH));
        }

        if (students.values().stream().anyMatch(Student::getExpelled)) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENT_IS_EXPELLED));
        }

        if (students.values().stream().anyMatch(Student::getLeaveOfAbsence)) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENT_IS_ON_A_BREAK));
        }

        List<Mark> toSave = new ArrayList<>();
        for (Map.Entry<String, Integer> mark : marks.getMarks().entrySet()) {
            Mark dto = new Mark();
            dto.setDate(marks.getDate());
            dto.setStudent(students.get(mark.getKey()));
            dto.setMark(Character.forDigit(mark.getValue(), 10));
            dto.setName(marks.getName());
            dto.setSubject(subject.get());
            toSave.add(dto);
        }

        marksRepo.saveAll(toSave);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT, " +
                  "T(ru.mephi.prepod.security.Role).PROFESSOR)")
    public ResponseEntity update(@RequestBody Marks newMarks) {

        Map<String, Mark> marks = marksRepo.findAllByNameAndSubjectAndGroupId(newMarks.getName(),
                newMarks.getSubjectId(), newMarks.getGroupId()).stream()
                .collect(Collectors.toMap(m -> m.getStudent().getId(), Function.identity()));

        if (!groupsRepo.existsById(newMarks.getGroupId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, GROUP_NOT_FOUND));
        }

        Optional<Subject> subject = subjectsRepo.findById(newMarks.getSubjectId());
        if (!subject.isPresent()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, SUBJECT_NOT_FOUND));
        }

        if (newMarks.getMarks().keySet().stream().anyMatch(id -> !studentsRepo.existsById(id))) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENT_NOT_FOUND));
        }

        Map<String, Student> students = studentsRepo.findAllByGroupId(newMarks.getGroupId()).stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));
        if (students.size() < newMarks.getMarks().keySet().size()
            || !students.keySet().containsAll(newMarks.getMarks().keySet())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENT_GROUP_MISMATCH));
        }

        if (students.values().stream().anyMatch(Student::getExpelled)) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENT_IS_EXPELLED));
        }

        if (students.values().stream().anyMatch(Student::getLeaveOfAbsence)) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENT_IS_ON_A_BREAK));
        }

        List<Mark> toSave = new ArrayList<>();
        for (Map.Entry<String, Integer> mark : newMarks.getMarks().entrySet()) {
            Mark dto = marks.get(mark.getKey());
            if (dto != null) {
                dto.setDate(newMarks.getDate());
                dto.setStudent(students.get(mark.getKey()));
                dto.setMark(Character.forDigit(mark.getValue(), 10));
                dto.setName(newMarks.getName());
                dto.setSubject(subject.get());
                toSave.add(dto);
            }
        }

        marksRepo.saveAll(toSave);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Marks {
        private String name;
        private String groupId;
        private String subjectId;
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate date;
        private Map<String, Integer> marks;
    }
}
