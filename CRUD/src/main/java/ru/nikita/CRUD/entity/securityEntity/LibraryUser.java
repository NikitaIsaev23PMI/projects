package ru.nikita.CRUD.entity.securityEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "library_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryUser {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    @Size(min = 8, max = 32,message = "пароль должен состоять минимум из 8 символов")
    private String password;

    @ManyToMany
    @JoinTable(name = "user_authority",
    joinColumns = @JoinColumn(name = "people_id"),
    inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private List<Authority> authorities;
}
