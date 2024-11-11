package ru.nikita.CRUD.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.nikita.CRUD.entity.Book;
import ru.nikita.CRUD.entity.People;
import ru.nikita.CRUD.services.BookService;
import ru.nikita.CRUD.services.EmailService;
import ru.nikita.CRUD.services.PeopleService;
import ru.nikita.CRUD.util.BookValidator;
import ru.nikita.CRUD.util.PeopleValidator;

import java.security.Principal;

@Controller
@RequestMapping("/book")
public class BookController {

    private BookService bookService;

    private PeopleService peopleService;

    private PeopleValidator peopleValidator;

    private BookValidator bookValidator;

    private EmailService emailService;

    @Autowired
    public BookController(BookService bookService, PeopleService peopleService,
                          PeopleValidator peopleValidator, BookValidator bookValidator
                          , EmailService emailService) {
        this.bookService = bookService;
        this.peopleService = peopleService;
        this.peopleValidator = peopleValidator;
        this.bookValidator = bookValidator;
        this.emailService = emailService;
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        //emailService.test();
        return "book/new";
    }

    @PostMapping()
    public String addBook(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                          @RequestParam("count") int count) {
        bookValidator.validate(book,count, bindingResult);
        if(bindingResult.hasErrors()) {
            return "book/new";
        }
        bookService.addBooks(book,count);
        return "redirect:/book/showall";
    }

    @GetMapping("/showall")
    public String showAllBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooksGroupedByTitle());
        return "/book/showall";
    }


    @GetMapping("/{id}")
    public String showBookById(@PathVariable("id") int id, Model model, @ModelAttribute("people") People people) {

        Book book = bookService.findBookById(id);
        model.addAttribute("book",book);

        if(book.getOwner() != null){
            model.addAttribute("owner",book.getOwner());
        }
        else{
            model.addAttribute("AllPeoples",peopleService.getAllPeople());
        }

        return "book/show";
    }


    @PatchMapping("/{id}/newOwner")
    public String updateOwner(@PathVariable("id") int bookId,
                              @ModelAttribute("people") People people,
                              BindingResult bindingResult, Model model) {

        people = peopleService.getPeopleById(people.getId()).get();

        peopleValidator.validate(bookId,people,bindingResult);

        if(bindingResult.hasErrors()) {
            model.addAttribute("book",bookService.findBookById(bookId));
            model.addAttribute("AllPeoples",peopleService.getAllPeople());
            return "book/show";
        }

        bookService.assignOwner(bookId,people);
        return "redirect:/book/showall";
    }

    @PatchMapping("/{id}/deleteOwner")
    public String deleteOwner(@PathVariable("id") int bookId) {
        bookService.deleteOwner(bookId);
        return "redirect:/book/showall";
    }


    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookService.deleteBookById(id);
        return "redirect:/book/showall";
    }

    @DeleteMapping("/deleteAllBooksByTitle")
    public String deleteAllBooksByTitle(String title) {
        bookService.deleteAllBooksByTitle(title);
        return "redirect:/book/showall";
    }

    @GetMapping("/{id}/edit")
    public String editBook(@PathVariable("id") int id,Model model) {
        Book book = bookService.findBookById(id);
        model.addAttribute("book",book);
        return "book/edit";
    }

    @PatchMapping("/{id}")
    public String edit(@PathVariable("id") int id,
                       @ModelAttribute("book") @Valid Book book,
                       BindingResult bindingResult, Model model){
        bookValidator.validate(book,bindingResult);
        if(bindingResult.hasErrors()) {
            model.addAttribute("book",bookService.findBookById(id));
            return "book/edit";
        }
        bookService.editBook(book,id);
        return "redirect:/book/showall";
    }

    @DeleteMapping("/deleteGroup/{title}")
    public String deleteGroup(@PathVariable("title") String title) {
        bookService.deleteGroupBooksByTitle(title);
        return "redirect:/book/showall";
    }
}
