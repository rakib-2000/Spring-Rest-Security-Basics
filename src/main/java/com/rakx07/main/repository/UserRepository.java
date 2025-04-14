package com.rakx07.main.repository;

import com.rakx07.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserNameIgnoreCase(String username);
}
