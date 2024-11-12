package com.worktrack.repository;
import com.worktrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);

    void deleteById(Long id);

    User findUserById(Long id);

    Optional<User> findFirstByRole(User.Role role);
}
