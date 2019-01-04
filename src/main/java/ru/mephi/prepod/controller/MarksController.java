package ru.mephi.prepod.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.prepod.dto.Mark;
import ru.mephi.prepod.repo.MarksRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/marks")
public class MarksController {

    private final MarksRepository marksRepo;

    @Autowired
    public MarksController(MarksRepository marksRepo) {
        this.marksRepo = marksRepo;
    }

    @GetMapping("/one")
    public GroupMarks getByNameAndSubjectAndGroup(@RequestParam("name") String name,
                                                  @RequestParam("subjectId") String subjectId,
                                                  @RequestParam("groupId") String groupId) {
        List<Mark> all = marksRepo.findAllByNameAndSubjectAndGroupId(name, subjectId, groupId);
        Map<String, Integer> marks = all.stream()
                .collect(Collectors.toMap(m -> m.getStudent().getId(), m -> Integer.valueOf(m.getMark())));
        return new GroupMarks(all.get(0).getName(), all.get(0).getDate(), marks);
    }

    @GetMapping("/forSubjectAndGroup")
    public List<GroupMarks> getAllForGroup(@RequestParam("subjectId") String subjectId,
                                           @RequestParam("groupId") String groupId) {
        List<GroupMarks> list = new ArrayList<>();
        List<Mark> all = marksRepo.findAllBySubjectIdAndGroupId(subjectId, groupId);
        Map<String, List<Mark>> byName = all.stream().collect(Collectors.groupingBy(Mark::getName));
        for (List<Mark> marks : byName.values()) {
            Map<String, Integer> byStudentId = marks.stream()
                    .collect(Collectors.toMap(m -> m.getStudent().getId(), m -> Integer.valueOf(m.getMark())));
            list.add(new GroupMarks(marks.get(0).getName(), marks.get(0).getDate(), byStudentId));
        }
        return list;
    }

    @Data
    @AllArgsConstructor
    private static class GroupMarks {
        private String name;
        private LocalDate date;
        private Map<String, Integer> marks;
    }
}
