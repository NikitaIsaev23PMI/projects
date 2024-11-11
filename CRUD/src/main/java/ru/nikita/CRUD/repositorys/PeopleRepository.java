package ru.nikita.CRUD.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nikita.CRUD.entity.People;

@Repository
public interface PeopleRepository extends JpaRepository<People, Integer> {
}
