package ru.nikita.CRUD.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.nikita.CRUD.entity.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByTitleStartingWith(String name);

    List<Book> getBooksByOwnerId(int id);

    void deleteBooksByTitle(String title);

    @Query("SELECT b FROM Book b WHERE b.id IN (SELECT MIN(b2.id) FROM Book b2 GROUP BY b2.title)")
    List<Book> getUniqBooks();

    List<Book> getAllByOwnerIsNotNull();
}
