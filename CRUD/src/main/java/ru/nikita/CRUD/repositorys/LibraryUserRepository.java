package ru.nikita.CRUD.repositorys;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.nikita.CRUD.entity.securityEntity.LibraryUser;

import java.util.Optional;

@Repository
public interface LibraryUserRepository extends CrudRepository<LibraryUser, Integer> {
    Optional<LibraryUser> findByUsername(String username);
}
