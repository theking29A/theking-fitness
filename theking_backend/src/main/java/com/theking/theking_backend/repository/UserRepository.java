package com.theking.theking_backend.repository;

import com.theking.theking_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccount(String account);
    Optional<User> findByEmail(String email);
    boolean existsByAccount(String account);
    boolean existsByEmail(String email);
    long countByRole(User.Role role);
    Page<User> findByAccountContainingOrNicknameContaining(String account, String nickname, Pageable pageable);
}
