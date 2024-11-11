package ru.nikita.CRUD.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.nikita.CRUD.entity.Book;
import ru.nikita.CRUD.entity.People;
import ru.nikita.CRUD.services.BookService;
import ru.nikita.CRUD.services.PeopleService;

@Component
public class PeopleValidator implements Validator {

    private PeopleService peopleService;

    private BookService bookService;

    @Autowired
    public PeopleValidator(PeopleService peopleService, BookService bookService) {
        this.peopleService = peopleService;
        this.bookService = bookService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return People.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        People people = (People) target;
        if(people.getBooks().size() == 5){
            errors.rejectValue("books", null, "Человек не может иметь более 5 книг");
        }

    }

    public void validate(int bookId, People people, Errors errors) {

        Book book = bookService.findBookById(bookId);
        boolean haveDuplicate = people.getBooks().stream()
                .anyMatch(a -> a.getTitle().equals(book.getTitle()));

        if(haveDuplicate){
            errors.rejectValue("books", "", "Данный читатель уже имеет такую книгу, наша библиотека позволяет брать только 1 экземпляр книги");
        }

        if(people.getBooks().size() == 5){
            errors.rejectValue("books", "", "Человек не может иметь более 5 книг");
        }
    }
}
