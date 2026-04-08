package br.com.oldtown.pharma.user.repository;

import br.com.oldtown.pharma.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@Param("email") String email);
    Page<User> findByActiveTrue(Pageable pageable);
    boolean existsByEmail(@Param("email") String email);
    boolean existsByEmailAndIdNot(@Param("email") String email, Long id);
}
