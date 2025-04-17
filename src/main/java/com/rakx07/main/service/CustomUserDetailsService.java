package com.rakx07.main.service;

import com.rakx07.main.model.Authority;
import com.rakx07.main.model.User;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    // Until the full method ends the database connection will not be closed.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByUsername(username);

        if(user == null)
        {
            throw new UsernameNotFoundException("No user found with this username: " + username);
        }

        // UserDetails object creation way.
        Set<GrantedAuthority> authorities = new HashSet<>();
        for(Authority authority : user.getAuthorities()) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority.getAuthority());
            authorities.add(simpleGrantedAuthority);
        }

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }
}
