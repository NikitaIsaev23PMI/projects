package ru.nikita.CRUD.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "People")
@NoArgsConstructor
@Getter
@Setter
public class People {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotNull(message = "Имя не может быть пустым")
    @Size(min = 2,max = 100, message = "Имя должно быть больше 2 символов и меньше 100")
    private String name;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @Column(name = "number")
    @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Номер должен быть в формате (+7/8)**********")
    private String number;

    @Column(name = "email")
    @Email(message = "Некоректный Email")
    private String email;

    @OneToMany(mappedBy = "owner")
    private List<Book> books;


    public People(String name, Date dateOfBirth, String number, String email) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.number = number;
        this.email = email;
    }
}
