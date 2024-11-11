package ru.nikita.CRUD.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.nikita.CRUD.entity.Book;
import ru.nikita.CRUD.repositorys.BookRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService {

    private BookRepository bookRepository;
    private BookService bookService;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    EmailService(BookRepository bookRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @Scheduled(fixedRateString = "PT12H")
    public void checkAllBooksAndSendEmails(){

        List<Book> allBooks = bookRepository.getAllByOwnerIsNotNull();
        bookService.expiredOrNot(allBooks);

        allBooks.stream()
                .forEach(book -> {
                    if (book.isExpiredOrNot()) {
                        sendEmailForOwner(book);
                    }
                });
    }

    private void sendEmailForOwner(Book book) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("IsaevNikita139@yandex.ru");
        simpleMailMessage.setTo(book.getOwner().getEmail());
        simpleMailMessage.setSubject("Уважаемый %s,Вы забыли вернуть книгу!".formatted(book.getOwner().getName()));
        simpleMailMessage.setText("Здравствуйте уважаемый %s, %s вы взяли в аренду книгу под названием %s" + "\n"
        +"с того времени прошло уже более месяца, и согласно регламенту нашей библиотеки вы должны ее вернуть в течении 7 дней"
                .formatted(book.getOwner().getName(),book.getTimeTaken().toString(),book.getTitle()));
        javaMailSender.send(simpleMailMessage);
    }
}
