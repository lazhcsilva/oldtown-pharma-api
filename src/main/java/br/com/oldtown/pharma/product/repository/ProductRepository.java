package br.com.oldtown.pharma.product.repository;

import br.com.oldtown.pharma.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(@Param("name") String name);
}
