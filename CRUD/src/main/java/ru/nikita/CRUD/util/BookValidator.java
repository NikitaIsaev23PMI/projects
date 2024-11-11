package ru.nikita.CRUD.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.nikita.CRUD.entity.Book;
import ru.nikita.CRUD.services.BookService;

@Component
public class BookValidator implements Validator {

    private BookService bookService;

    @Autowired
    public BookValidator(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }

    public void validate(Book book, int count, Errors errors){
        String title = book.getTitle();
        int countBooks = (int)bookService.getAllBooks().stream()
                .filter(b -> b.getTitle().equals(title))
                .count();
        if(countBooks + count > 100){
            errors.rejectValue("title", "","Библиотека не может содержать больше 100 одинаковых книг");
        }
    }
}
