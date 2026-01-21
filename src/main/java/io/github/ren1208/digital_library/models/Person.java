package io.github.ren1208.digital_library.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * @author Artyom Semenchenko
 */

@Data
@Entity
@Table(name = "Person")
public class Person {

    @Id
    @Column(name = "person_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int personId;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Book> books;

    @Pattern(
            regexp = "^(?=.{4,50}$)(?!\\s*$)[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+(?:\\s[А-ЯЁ][а-яё]+)?$",
            message = "Неверный формат ФИО. Требования: 1) Непустое поле; 2) От 4 до 50 символов; 3) Формат 'Фамилия Имя (Отчество)'; 4) Только кириллица, каждое слово с заглавной буквы"
    )
    @Column(name = "name")
    private String name;

    @Min(value = 1900, message = "Год рождения должен быть больше 1900.")
    @Max(value = 2024, message = "Год рождения должен быть меньше 2025.")
    @Column(name = "year_of_birth")
    private int yearOfBirth;

    @Column(name = "password")
    @NotEmpty(message = "Пароль не может быть пустым")
    private String password;

    @Column(name = "role")
    private String role;

}
