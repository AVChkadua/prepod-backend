package ru.mephi.prepod.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.prepod.dto.Attendance;
import ru.mephi.prepod.dto.Lesson;
import ru.mephi.prepod.repo.AttendanceRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceRepository attendanceRepo;

    @Autowired
    public AttendanceController(AttendanceRepository attendanceRepo) {
        this.attendanceRepo = attendanceRepo;
    }

    @GetMapping("/one")
    public Map<String, Boolean> getAttendanceForDateAndTime(@RequestParam("date") LocalDate date,
                                                            @RequestParam("startTime") String startTime,
                                                            @RequestParam("endTime") String endTime,
                                                            @RequestParam("groupId") String groupId) {
        return attendanceRepo.findAllByDateAndSubjectIdAndGroupId(date, startTime, endTime, groupId).stream()
                .collect(Collectors.toMap(a -> a.getStudent().getId(), Attendance::getIsPresent));
    }

    @GetMapping("/forSubjectAndGroup")
    public List<GroupAttendance> getGroupAttendance(@RequestParam("subjectId") String subjectId,
                                                    @RequestParam("groupId") String groupId) {
        List<Attendance> attendance = attendanceRepo.findAllBySubjectIdAndGroupId(subjectId, groupId);
        Map<Lesson, List<Attendance>> byBells =
                attendance.stream().collect(Collectors.groupingBy(Attendance::getLesson));
        List<GroupAttendance> list = new ArrayList<>();
        for (Map.Entry<Lesson, List<Attendance>> entry : byBells.entrySet()) {
            Map<String, Boolean> values = entry.getValue().stream()
                    .collect(Collectors.toMap(a -> a.getStudent().getId(), Attendance::getIsPresent));
            GroupAttendance groupAttendance = new GroupAttendance(entry.getValue().get(0).getDate(),
                    entry.getKey().getStartBell().getTime(),
                    entry.getKey().getEndBell().getTime(), values);
            list.add(groupAttendance);
        }
        return list;
    }

    @Data
    @AllArgsConstructor
    private static class GroupAttendance {
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private Map<String, Boolean> attendance;
    }
}
