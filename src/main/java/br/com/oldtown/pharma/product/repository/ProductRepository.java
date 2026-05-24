package br.com.oldtown.pharma.product.repository;

import br.com.oldtown.pharma.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByName(@Param("name") String name);
    boolean existsByNameIgnoreCase(@Param("name") String name);
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, Long id);
}
