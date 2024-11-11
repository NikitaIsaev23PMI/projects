package ru.nikita.CRUD.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikita.CRUD.entity.Book;
import ru.nikita.CRUD.entity.People;
import ru.nikita.CRUD.repositorys.BookRepository;
import ru.nikita.CRUD.repositorys.PeopleRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookService {

    private BookRepository bookRepository;

    private PeopleRepository peopleRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> findByTitleStartWith(String title) {
        return bookRepository.findByTitleStartingWith(title);
    }

    public Book findBookById(int id) {
        if(bookRepository.findById(id).isPresent()){
            return bookRepository.findById(id).get();
        }
        else throw new RuntimeException(); //TODO Доработать собственное исключение
    }

    public List<Book> getAllSortedBooks(){
        return bookRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Map<String, List<Book>> getAllBooksGroupedByTitle() {
        return bookRepository.findAll().stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.groupingBy(Book::getTitle, TreeMap::new, Collectors.toList()));
    }

    @Transactional
    public void deleteBookById(int id) {
        bookRepository.deleteById(id);
    }

    @Transactional
    public void assignOwner(int bookId, People people) {

        Book book = findBookById(bookId);
        book.setOwner(people);
        book.setTimeTaken(new Date());

    }

    @Transactional
    public void deleteOwner(int bookId) {
        Book book = findBookById(bookId);
        book.setOwner(null);
        book.setTimeTaken(null);
    }


    @Transactional
    public void addBooks(Book book, int count) {

        for(int i = 0; i < count; i++){

            Book newBook = new Book();
            newBook.setTitle(book.getTitle());
            newBook.setAuthor(book.getAuthor());
            newBook.setDateOfPublishing(book.getDateOfPublishing());

            save(newBook);
        }
    }

    /**
     * удаляет все книги с одинаковым именем
     * @param title
     */
    public void deleteAllBooksByTitle(String title) {
        bookRepository.deleteBooksByTitle(title);
    }

    public List<Book> getAllUniqBooks() {
        return bookRepository.getUniqBooks();
    }

    public boolean checkTheBookForDuplicate(int bookId, People people) {
        Book book = findBookById(bookId);
        return people.getBooks().stream()
                .anyMatch(a -> a.getTitle().equals(book.getTitle()));
    }

    public void editBook(Book book, int id) {
        book.setId(id);
        bookRepository.save(book);
    }

    @Transactional
    public void deleteGroupBooksByTitle(String title) {
        bookRepository.deleteBooksByTitle(title);
    }

    public void check(){
        List<Book> books = getAllBooks().stream()
                .filter(book -> book.getOwner() != null)
                .toList();

        expiredOrNot(books);

        books.stream()
                .forEach(book ->
                {
                    if (book.isExpiredOrNot()){
                        People people = book.getOwner();
                        //TODO метод с отправкой Email
                    }

                });

    }


    @Transactional
    public void expiredOrNot(List<Book> books) {
        Instant now = Instant.now();
        for (Book book : books) {
            Instant timeTaken = book.getTimeTaken().toInstant();
            if (now.isAfter(timeTaken.plus(30, ChronoUnit.DAYS))) {
                book.setExpiredOrNot(true);
            }
        }
    }
}
