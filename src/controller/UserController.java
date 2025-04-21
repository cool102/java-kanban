package controller;

import exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null || !users.containsKey(newUser.getId())) {
            log.warn("Ошибка при обновлении пользователя: id не найден: {}", newUser.getId());
            throw new ValidationException("Пользователь с id: " + newUser.getId() + " не найден");
        }
        validateUser(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Обновлён пользователь: {}", newUser);
        return newUser;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: некорректная почта: {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: некорректный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin()); // Автозамена имени на логин
        }
        LocalDate birthdate = user.getBirthdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (birthdate.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: дата рождения в будущем: {}", birthdate);
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private long getNextId() {
        return users.keySet().stream().mapToLong(Long::longValue).max().orElse(0L) + 1;
    }
}
