package ru.nikita.CRUD.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.nikita.CRUD.entity.securityEntity.LibraryUser;
import ru.nikita.CRUD.repositorys.LibraryUserRepository;

@Component
public class UserValidator implements Validator {

    private LibraryUserRepository libraryUserRepository;

    @Autowired
    public UserValidator(LibraryUserRepository libraryUserRepository) {
        this.libraryUserRepository = libraryUserRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return  User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LibraryUser user = (LibraryUser)target;
        String username = user.getUsername();

        if(libraryUserRepository.findByUsername(username).isPresent()){
            errors.rejectValue("username", null, "Данный никнейм уже занят, пожалуйста выберите другой");
        }
    }

}
