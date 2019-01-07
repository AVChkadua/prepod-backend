package ru.mephi.prepod.controller;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mephi.prepod.dto.User;
import ru.mephi.prepod.repo.UsersRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersController {

    private static final String ERROR = "error";
    private static final String USER_NOT_FOUND = "User not found";

    private final UsersRepository usersRepo;

    @Autowired
    public UsersController(UsersRepository usersRepo) {
        this.usersRepo = usersRepo;
    }

    @GetMapping
    public Iterable<User> getAll() {
        return usersRepo.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> getById(@PathVariable("id") String id) {
        return usersRepo.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public User create(@RequestBody User user) {
        return usersRepo.save(user);
    }

    @PutMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public ResponseEntity update(@RequestBody User user) {
        if (!usersRepo.existsById(user.getId())) {
            return ResponseEntity.badRequest().body(ImmutableMap.of(ERROR, USER_NOT_FOUND));
        }
        return ResponseEntity.ok(usersRepo.save(user));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority(T(ru.mephi.prepod.security.Role).ADMIN)")
    public void delete(@RequestBody List<String> ids) {
        ids.forEach(usersRepo::deleteById);
    }
}
