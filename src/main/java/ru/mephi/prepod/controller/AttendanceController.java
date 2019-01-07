package ru.mephi.prepod.controller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.common.DatabaseExceptionHandler;
import ru.mephi.prepod.common.LocalDateDeserializer;
import ru.mephi.prepod.common.LocalDateSerializer;
import ru.mephi.prepod.dto.Attendance;
import ru.mephi.prepod.dto.Lesson;
import ru.mephi.prepod.dto.Student;
import ru.mephi.prepod.repo.AttendanceRepository;
import ru.mephi.prepod.repo.LessonsRepository;
import ru.mephi.prepod.repo.StudentsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private static final String ERROR = "error";
    private static final String NO_LESSON_FOUND = "No lesson with such id found";
    private static final String STUDENTS_MISSING = "Not all students are present";
    private static final String STUDENTS_GROUP_MISMATCH = "Students from the other group found";

    private final AttendanceRepository attendanceRepo;

    private final LessonsRepository lessonsRepo;

    private final StudentsRepository studentsRepo;

    @Autowired
    public AttendanceController(AttendanceRepository attendanceRepo, LessonsRepository lessonsRepo,
                                StudentsRepository studentsRepo) {
        this.attendanceRepo = attendanceRepo;
        this.lessonsRepo = lessonsRepo;
        this.studentsRepo = studentsRepo;
    }

    @GetMapping("/byGroupAndLessonAndDate")
    @PreAuthorize("hasAnyAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT, " +
                  "T(ru.mephi.prepod.security.Role).PROFESSOR)")
    public Map<String, Boolean> getByGroupAndLessonAndDate(
            @RequestParam("groupId") String groupId,
            @RequestParam("lessonId") String lessonId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceRepo.findAllByDateAndLessonIdAndGroupId(date, lessonId, groupId).stream()
                .collect(Collectors.toMap(a -> a.getStudent().getId(), Attendance::getIsPresent));
    }

    @GetMapping("/byGroupAndLesson")
    @PreAuthorize("hasAnyAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT, " +
                  "T(ru.mephi.prepod.security.Role).PROFESSOR)")
    public List<DateAttendance> getByGroupAndLesson(@RequestParam("groupId") String groupId,
                                                    @RequestParam("lessonId") String lessonId) {
        List<Attendance> attendance = attendanceRepo.findAllByLessonIdAndGroupId(lessonId, groupId);
        Map<LocalDate, List<Attendance>> byDate =
                attendance.stream().collect(Collectors.groupingBy(Attendance::getDate));
        List<DateAttendance> list = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Attendance>> entry : byDate.entrySet()) {
            Map<String, Boolean> values = entry.getValue().stream()
                    .collect(Collectors.toMap(a -> a.getStudent().getId(), Attendance::getIsPresent));
            DateAttendance dateAttendance = new DateAttendance(entry.getValue().get(0).getDate(), values);
            list.add(dateAttendance);
        }
        return list;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT, " +
                  "T(ru.mephi.prepod.security.Role).PROFESSOR)")
    public ResponseEntity save(@RequestBody LessonDateAttendance attendance) {
        Optional<Lesson> lesson = lessonsRepo.findById(attendance.getLessonId());
        if (!lesson.isPresent()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, NO_LESSON_FOUND));
        }

        List<Student> studentsInGroup = studentsRepo.findAllByGroupId(attendance.getGroupId());
        if (studentsInGroup.size() != attendance.getAttendance().size()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENTS_MISSING));
        } else if (!studentsInGroup.stream().map(Student::getId).collect(Collectors.toSet())
                .equals(attendance.getAttendance().keySet())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENTS_GROUP_MISMATCH));
        }

        List<Attendance> toSave = new ArrayList<>();
        for (Student student : studentsInGroup) {
            Attendance newEntry = new Attendance();
            newEntry.setDate(attendance.getDate());
            newEntry.setStudent(student);
            newEntry.setIsPresent(attendance.getAttendance().get(student.getId()));
            newEntry.setLesson(lesson.get());
            toSave.add(newEntry);
        }

        attendanceRepo.saveAll(toSave);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority(T(ru.mephi.prepod.security.Role).HEAD_OF_DEPARTMENT, " +
                  "T(ru.mephi.prepod.security.Role).PROFESSOR)")
    public ResponseEntity update(@RequestBody LessonDateAttendance attendance) {
        Optional<Lesson> lesson = lessonsRepo.findById(attendance.getLessonId());
        if (!lesson.isPresent()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, NO_LESSON_FOUND));
        }

        List<Student> studentsInGroup = studentsRepo.findAllByGroupId(attendance.getGroupId());
        if (!studentsInGroup.stream().map(Student::getId).collect(Collectors.toSet())
                .equals(attendance.getAttendance().keySet())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, STUDENTS_GROUP_MISMATCH));
        }

        List<Attendance> existing = attendanceRepo.findAllByDateAndLessonIdAndGroupId(attendance.getDate(),
                attendance.getLessonId(), attendance.getGroupId());
        existing.forEach(ex -> ex.setIsPresent(attendance.getAttendance().get(ex.getStudent().getId())));

        attendanceRepo.saveAll(existing);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handler(DataIntegrityViolationException e) {
        return DatabaseExceptionHandler.handle(e);
    }

    @Data
    @AllArgsConstructor
    private static class DateAttendance {
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate date;
        private Map<String, Boolean> attendance;
    }

    @Data
    private static class LessonDateAttendance {
        private String lessonId;
        private String groupId;
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate date;
        private Map<String, Boolean> attendance;
    }
}
