package ru.nikita.CRUD.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.nikita.CRUD.entity.People;
import ru.nikita.CRUD.services.PeopleService;
import ru.nikita.CRUD.util.PeopleValidator;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private PeopleService peopleService;

    private PeopleValidator peopleValidator;

    @Autowired
    PeopleController(PeopleService peopleService, PeopleValidator peopleValidator) {
        this.peopleService = peopleService;
        this.peopleValidator = peopleValidator;
    }

    /*@GetMapping("/all")
    public String all(){
        return "people/all";
    }*/

    @GetMapping("/new")
    public String newPeople(@ModelAttribute("people") People people){
        return "people/new";
    }

    @PostMapping()
    public String savePeople(@ModelAttribute("people") @Valid People people, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "people/new";
        }
        peopleService.save(people);
        return "redirect:/people/showAll";
    }

    @GetMapping("/{id}/edit")
    public String editPeople(@PathVariable int id, Model model) {
        model.addAttribute("people", peopleService.getPeopleById(id).get());
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String editPeople(@PathVariable("id") int id, @ModelAttribute("people") @Valid People people,
                             BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("people", peopleService.getPeopleById(id).get());
            return "people/edit";
        }
        peopleService.updatePeopleById(id,people);
        return "redirect:/people/showAll";
    }

    @GetMapping("/{id}")
    public String showPeople(@PathVariable int id, Model model) {
        People people = peopleService.getPeopleById(id).get();
        model.addAttribute("people", people);

        if(people.getBooks() != null && people.getBooks().size() > 0) {
            model.addAttribute("books", peopleService.updatedPeopleBooks(people));
        }
        else {
            model.addAttribute("noBooks", "");
        }
        return "people/show";
    }

    @GetMapping("/showAll")
    public String showAllPeople(Model model) {
        model.addAttribute("peoples", peopleService.getAllSortedPeople());
        return "people/showAll";
    }

    @DeleteMapping("/{id}")
    public String deletePeople(@PathVariable("id") int id) {
        peopleService.deletePeopleById(id);
        return "redirect:/people/showAll";
    }
}
