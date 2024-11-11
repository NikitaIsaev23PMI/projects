package ru.nikita.CRUD.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikita.CRUD.entity.securityEntity.Authority;
import ru.nikita.CRUD.repositorys.LibraryUserRepository;

@Service
@RequiredArgsConstructor
public class LibraryUserDetailService implements UserDetailsService{

    private LibraryUserRepository libraryUserRepository;

    @Autowired
    public LibraryUserDetailService(LibraryUserRepository libraryUserRepository) {
        this.libraryUserRepository = libraryUserRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println(libraryUserRepository.findByUsername(username).get().getUsername());

        System.out.println(libraryUserRepository.findByUsername(username).get().getPassword());

        System.out.println(libraryUserRepository.findByUsername(username).get().getAuthorities());

        return libraryUserRepository.findByUsername(username)
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getAuthorities().stream()
                                .map(Authority::getAuthority)
                                .map(SimpleGrantedAuthority::new).toList())
                        .build())
                        .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
    }
}
