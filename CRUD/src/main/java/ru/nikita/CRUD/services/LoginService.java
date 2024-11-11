package ru.nikita.CRUD.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikita.CRUD.entity.securityEntity.Authority;
import ru.nikita.CRUD.entity.securityEntity.LibraryUser;
import ru.nikita.CRUD.repositorys.AuthorityRepository;
import ru.nikita.CRUD.repositorys.LibraryUserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LoginService {

    private LibraryUserRepository libraryUserRepository;

    private AuthorityRepository authorityRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    LoginService(LibraryUserRepository libraryUserRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.libraryUserRepository = libraryUserRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(LibraryUser user) {

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        Optional<Authority> authority = authorityRepository.findByAuthority("ROLE_ADMIN");
        if (authority.isPresent()) {
            user.setAuthorities(List.of(authority.get()));
            libraryUserRepository.save(user);
        }
        else {
            throw new RuntimeException("User Authority not found");
        }
    }
}
