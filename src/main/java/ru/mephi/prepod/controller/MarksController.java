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
import java.util.Objects;
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
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).GET_MARKS)")
    public Marks getByNameAndSubjectAndGroup(@RequestParam("name") String name,
                                             @RequestParam("subjectId") String subjectId,
                                             @RequestParam("groupId") String groupId) {
        List<Mark> all = marksRepo.findAllByNameAndSubjectAndGroupId(name, subjectId, groupId);
        Map<String, Integer> marks = all.stream()
                .collect(Collectors.toMap(m -> m.getStudent().getId(), m -> Character.getNumericValue(m.getMark())));
        return new Marks(all.get(0).getName(),
                all.get(0).getStudent().getGroup().getId(),
                all.get(0).getSubject().getId(),
                all.get(0).getDate(),
                marks);
    }

    @GetMapping("/bySubjectAndGroup")
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).GET_MARKS)")
    public List<Marks> getBySubjectAndGroup(@RequestParam("subjectId") String subjectId,
                                            @RequestParam("groupId") String groupId) {
        List<Marks> list = new ArrayList<>();
        List<Mark> all = marksRepo.findAllBySubjectIdAndGroupId(subjectId, groupId);
        Map<String, List<Mark>> byName = all.stream().collect(Collectors.groupingBy(Mark::getName));
        for (List<Mark> marks : byName.values()) {
            Map<String, Integer> byStudentId = marks.stream()
                    .collect(Collectors.toMap(m -> m.getStudent().getId(),
                            m -> Character.getNumericValue(m.getMark())));
            list.add(new Marks(marks.get(0).getName(),
                    all.get(0).getStudent().getGroup().getId(),
                    all.get(0).getSubject().getId(),
                    marks.get(0).getDate(), byStudentId));
        }
        return list;
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_MARKS)")
    public ResponseEntity create(@RequestBody Marks marks) {
        ResponseEntity error = validate(marks);
        if (error != null) {
            return error;
        }

        List<Mark> toSave = marks.getMarks().entrySet().stream().map(mark -> {
            Mark dto = new Mark();
            return setFields(dto, marks, mark);
        }).collect(Collectors.toList());

        marksRepo.saveAll(toSave);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Authority).EDIT_MARKS)")
    public ResponseEntity update(@RequestBody Marks newMarks) {

        Map<String, Mark> marks = marksRepo.findAllByNameAndSubjectAndGroupId(newMarks.getName(),
                newMarks.getSubjectId(), newMarks.getGroupId()).stream()
                .collect(Collectors.toMap(m -> m.getStudent().getId(), Function.identity()));

        ResponseEntity error = validate(newMarks);
        if (error != null) {
            return error;
        }

        List<Mark> toSave = newMarks.getMarks().entrySet().stream().map(mark -> {
            Mark dto = marks.get(mark.getKey());
            if (dto != null) {
                setFields(dto, newMarks, mark);
            }
            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        marksRepo.saveAll(toSave);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }

    private ResponseEntity validate(Marks marks) {
        if (!groupsRepo.existsById(marks.getGroupId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, GROUP_NOT_FOUND));
        }

        if (!subjectsRepo.existsById(marks.getSubjectId())) {
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

        return null;
    }

    private Mark setFields(Mark dto, Marks newMarks, Map.Entry<String, Integer> studentMark) {
        Student student = new Student();
        student.setId(studentMark.getKey());
        Subject subject = new Subject();
        subject.setId(newMarks.getSubjectId());
        dto.setDate(newMarks.getDate());
        dto.setName(newMarks.getName());
        dto.setStudent(student);
        dto.setMark(Character.forDigit(studentMark.getValue(), 10));
        dto.setSubject(subject);
        return dto;
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
