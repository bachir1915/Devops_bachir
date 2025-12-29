package com.isi.devops.repository;

import com.isi.devops.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests unitaires pour ProductRepository")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product laptop;
    private Product phone;
    private Product tablet;

    @BeforeEach
    void setUp() {
        laptop = Product.builder()
                .name("Laptop Gaming")
                .description("Laptop haute performance")
                .price(1500.00)
                .quantity(5)
                .build();

        phone = Product.builder()
                .name("Smartphone Pro")
                .description("Smartphone dernière génération")
                .price(899.99)
                .quantity(20)
                .build();

        tablet = Product.builder()
                .name("Tablet Ultra")
                .description("Tablet légère et performante")
                .price(599.99)
                .quantity(0)
                .build();

        entityManager.persist(laptop);
        entityManager.persist(phone);
        entityManager.persist(tablet);
        entityManager.flush();
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase - Trouve les produits par nom partiel")
    void findByNameContainingIgnoreCase_ShouldFindMatchingProducts() {
        // Act
        List<Product> result = productRepository.findByNameContainingIgnoreCase("pro");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Smartphone Pro");
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase - Insensible à la casse")
    void findByNameContainingIgnoreCase_ShouldBeCaseInsensitive() {
        // Act
        List<Product> upperCase = productRepository.findByNameContainingIgnoreCase("LAPTOP");
        List<Product> lowerCase = productRepository.findByNameContainingIgnoreCase("laptop");
        List<Product> mixedCase = productRepository.findByNameContainingIgnoreCase("LaPtOp");

        // Assert
        assertThat(upperCase).hasSize(1);
        assertThat(lowerCase).hasSize(1);
        assertThat(mixedCase).hasSize(1);
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase - Retourne liste vide si aucun match")
    void findByNameContainingIgnoreCase_WhenNoMatch_ShouldReturnEmptyList() {
        // Act
        List<Product> result = productRepository.findByNameContainingIgnoreCase("xyz");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByPriceLessThanEqual - Trouve les produits par prix maximum")
    void findByPriceLessThanEqual_ShouldFindProductsBelowPrice() {
        // Act
        List<Product> result = productRepository.findByPriceLessThanEqual(900.00);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Smartphone Pro", "Tablet Ultra");
    }

    @Test
    @DisplayName("findByQuantityGreaterThan - Trouve les produits avec stock suffisant")
    void findByQuantityGreaterThan_ShouldFindProductsWithStock() {
        // Act
        List<Product> result = productRepository.findByQuantityGreaterThan(0);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Laptop Gaming", "Smartphone Pro");
    }

    @Test
    @DisplayName("findByQuantityGreaterThan - Retourne liste vide si stock insuffisant")
    void findByQuantityGreaterThan_WhenNoStock_ShouldReturnEmptyList() {
        // Act
        List<Product> result = productRepository.findByQuantityGreaterThan(100);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save - Sauvegarde un nouveau produit")
    void save_ShouldPersistProduct() {
        // Arrange
        Product newProduct = Product.builder()
                .name("New Product")
                .description("Description")
                .price(100.00)
                .quantity(10)
                .build();

        // Act
        Product saved = productRepository.save(newProduct);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(entityManager.find(Product.class, saved.getId())).isNotNull();
    }

    @Test
    @DisplayName("findAll - Retourne tous les produits")
    void findAll_ShouldReturnAllProducts() {
        // Act
        List<Product> result = productRepository.findAll();

        // Assert
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("deleteById - Supprime un produit par son ID")
    void deleteById_ShouldRemoveProduct() {
        // Act
        productRepository.deleteById(laptop.getId());
        entityManager.flush();

        // Assert
        assertThat(entityManager.find(Product.class, laptop.getId())).isNull();
        assertThat(productRepository.findAll()).hasSize(2);
    }
}
