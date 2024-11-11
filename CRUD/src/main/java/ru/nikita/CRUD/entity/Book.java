package ru.nikita.CRUD.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "Book")
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    @NotNull(message = "Поле названия не может быть пустым")
    @Size(min = 2, message = "Название книги не может быть меньше 2 символов")
    @Size(max = 100, message = "Название книги не может быть больше 100 символов")
    private String title;

    @Column(name = "author")
    @NotNull(message = "имя автора не может быть пустым")
    private String author;

    @Column(name = "date_of_publishing")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Книга не может быть созданной в будущем времени")
    private Date dateOfPublishing;

    @ManyToOne
    @JoinColumn(name = "people_id", referencedColumnName = "id")
    private People owner;

    @Column(name = "time_taken")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeTaken;

    @Transient
    private boolean ExpiredOrNot;

    public Book(String title, String author, Date dateOfPublishing) {
        this.title = title;
        this.author = author;
        this.dateOfPublishing = dateOfPublishing;
    }

    public String getOwnerName(){
        if(owner != null){
            return "Сейчас книга у " + this.owner.getName();
        }
        else {
            return "Книга свободна";
        }

    }
}
