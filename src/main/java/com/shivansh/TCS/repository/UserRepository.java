package com.shivansh.TCS.repository;

import java.util.List;
import java.util.Optional;

import com.shivansh.TCS.model.User;
import com.shivansh.TCS.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findById(Long id);

    List<User> findByName(String name);

    List<User> findByNameContaining(String name);

    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    Optional<User> findByEmailAndPassword(String email, String password);
}
