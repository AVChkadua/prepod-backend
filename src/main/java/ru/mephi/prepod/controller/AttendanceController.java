package ru.mephi.prepod.controller;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.dto.Attendance;
import ru.mephi.prepod.dto.Lesson;
import ru.mephi.prepod.dto.Student;
import ru.mephi.prepod.repo.AttendanceRepository;
import ru.mephi.prepod.repo.LessonsRepository;
import ru.mephi.prepod.repo.StudentsRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private static final String ERROR = "error";
    private static final String NO_LESSON_FOUND = "No lesson with such id found";
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

    @GetMapping("/forDateAndLessonAndGroup")
    public Map<String, Boolean> getGroupAttendanceForLessonAndDate(@RequestParam("date") LocalDate date,
                                                                   @RequestParam("lessonId") String lessonId,
                                                                   @RequestParam("groupId") String groupId) {
        return attendanceRepo.findAllByDateAndLessonIdAndGroupId(date, lessonId, groupId).stream()
                .collect(Collectors.toMap(a -> a.getStudent().getId(), Attendance::getIsPresent));
    }

    @GetMapping("/forLessonAndGroup")
    public List<DateAttendance> getGroupAttendance(@RequestParam("lessonId") String lessonId,
                                                   @RequestParam("groupId") String groupId) {
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
    public ResponseEntity save(@RequestBody LessonDateAttendance attendance) {
        Optional<Lesson> lesson = lessonsRepo.findById(attendance.getLessonId());
        if (!lesson.isPresent()) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, NO_LESSON_FOUND));
        }

        List<Student> studentsInGroup = studentsRepo.findAllByGroupId(attendance.getGroupId());
        if (!studentsInGroup.stream().map(Student::getId).collect(Collectors.toSet())
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

    @Data
    @AllArgsConstructor
    private static class DateAttendance {
        private LocalDate date;
        private Map<String, Boolean> attendance;
    }

    @Data
    private static class LessonDateAttendance {
        private String lessonId;
        private String groupId;
        private LocalDate date;
        private Map<String, Boolean> attendance;
    }
}
