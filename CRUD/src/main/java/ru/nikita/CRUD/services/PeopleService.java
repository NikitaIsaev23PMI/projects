package ru.nikita.CRUD.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikita.CRUD.entity.Book;
import ru.nikita.CRUD.entity.People;
import ru.nikita.CRUD.repositorys.BookRepository;
import ru.nikita.CRUD.repositorys.PeopleRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private PeopleRepository peopleRepository;

    private BookRepository bookRepository;

    private BookService bookService;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, BookRepository bookRepository, BookService bookService) {
        this.peopleRepository = peopleRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    public Optional<People> getPeopleById(int id) {
        return peopleRepository.findById(id);
    }

    public List<Book> getBooksByPeopleId(int id) {
        return bookRepository.getBooksByOwnerId(id);
    }

    @Transactional
    public void save(People people) {
        peopleRepository.save(people);
    }

    @Transactional
    public void assignBook(int bookId, int peopleId){

        Optional<Book> book = bookRepository.findById(bookId);
        Optional<People> people = peopleRepository.findById(peopleId);

        if(book.isPresent() && people.isPresent()){
            book.get().setOwner(people.get());
        }
        else throw new RuntimeException(); //TODO Обработать исключение потом
    }



    @Transactional
    public void updatePeopleById(int id, People people) {
        people.setId(id);
        peopleRepository.save(people);
    }

    public List<People> getAllPeople() {
        return peopleRepository.findAll();
    }



    public List<Book> updatedPeopleBooks(People people) {

        List<Book> books = getBooksByPeopleId(people.getId());

        bookService.expiredOrNot(books);

        return books;
    }

    public List<People> getAllSortedPeople() {
        return getAllPeople().stream()
                .sorted(Comparator.comparing(People::getName))
                .toList();
    }

    @Transactional
    public void deletePeopleById(int id) {
        peopleRepository.deleteById(id);
    }
}
