package br.com.oldtown.pharma.user.repository;

import br.com.oldtown.pharma.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(@Param("email") String email);
    boolean existsByEmail(@Param("email") String email);
    List<User> findByActiveTrue();
}
