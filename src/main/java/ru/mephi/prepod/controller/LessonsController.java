package ru.mephi.prepod.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.prepod.dto.*;
import ru.mephi.prepod.repo.LessonsRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/timetable")
public class LessonsController {

    private final LessonsRepository lessonsRepo;

    @Autowired
    public LessonsController(LessonsRepository lessonsRepo) {
        this.lessonsRepo = lessonsRepo;
    }

    @GetMapping("/group/{id}")
    public TimetableInfo getGroupTimetable(@PathVariable("id") String groupId) {
        Map<WeekType, List<Lesson>> byWeekType = lessonsRepo.findAllByGroupId(groupId).stream()
                .collect(Collectors.groupingBy(Lesson::getWeekType));
        return new TimetableInfo(buildWeekTimetable(byWeekType.get(WeekType.ALL)),
                buildWeekTimetable(byWeekType.get(WeekType.EVEN)),
                buildWeekTimetable(byWeekType.get(WeekType.ODD)));
    }

    @GetMapping("/professor/{id}")
    public TimetableInfo getProfessorTimetable(@PathVariable("id") String professorId) {
        Map<WeekType, List<Lesson>> byWeekType = lessonsRepo.findAllByProfessorId(professorId).stream()
                .collect(Collectors.groupingBy(Lesson::getWeekType));
        return new TimetableInfo(buildWeekTimetable(byWeekType.get(WeekType.ALL)),
                buildWeekTimetable(byWeekType.get(WeekType.EVEN)),
                buildWeekTimetable(byWeekType.get(WeekType.ODD)));
    }

    private List<DayTimetable> buildWeekTimetable(List<Lesson> lessons) {
        Map<DayOfWeek, List<Lesson>> byDayOfWeek =
                lessons.stream().collect(Collectors.groupingBy(Lesson::getDayOfWeek));
        List<DayTimetable> daysTimetable = new ArrayList<>();
        for (Map.Entry<DayOfWeek, List<Lesson>> entry : byDayOfWeek.entrySet()) {
            daysTimetable.add(new DayTimetable(entry.getKey(),
                    entry.getValue().stream()
                            .map(this::buildLessonInfo)
                            .collect(Collectors.toList())));
        }
        return daysTimetable;
    }

    private LessonInfo buildLessonInfo(Lesson lesson) {
        return new LessonInfo(lesson.getSubject(), lesson.getProfessors(), lesson.getGroups(), lesson.getLocation(),
                lesson.getStartBell().getTime(), lesson.getEndBell().getTime());
    }

    @Data
    @AllArgsConstructor
    private static class TimetableInfo {
        private List<DayTimetable> all;
        private List<DayTimetable> odd;
        private List<DayTimetable> even;
    }

    @Data
    @AllArgsConstructor
    private static class DayTimetable {
        private DayOfWeek dayOfWeek;
        private List<LessonInfo> events;
    }

    @Data
    @AllArgsConstructor
    private static class LessonInfo {
        private Subject subject;
        private List<Professor> professor;
        private List<Group> group;
        private Location location;
        private LocalTime startTime;
        private LocalTime endTime;
    }
}
