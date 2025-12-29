package com.isi.devops.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests de validation pour ProductRequest")
class ProductRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validation réussie avec données valides")
    void whenAllFieldsValid_ShouldHaveNoViolations() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Produit Test")
                .description("Description")
                .price(99.99)
                .quantity(10)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Validation échoue si le nom est vide")
    void whenNameIsBlank_ShouldHaveViolation() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("")
                .price(99.99)
                .quantity(10)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Le nom est obligatoire");
    }

    @Test
    @DisplayName("Validation échoue si le nom est null")
    void whenNameIsNull_ShouldHaveViolation() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name(null)
                .price(99.99)
                .quantity(10)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validation échoue si le prix est null")
    void whenPriceIsNull_ShouldHaveViolation() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Produit")
                .price(null)
                .quantity(10)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Le prix est obligatoire");
    }

    @Test
    @DisplayName("Validation échoue si le prix est négatif")
    void whenPriceIsNegative_ShouldHaveViolation() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Produit")
                .price(-10.00)
                .quantity(10)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Le prix doit être positif");
    }

    @Test
    @DisplayName("Validation échoue si le prix est zéro")
    void whenPriceIsZero_ShouldHaveViolation() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Produit")
                .price(0.0)
                .quantity(10)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("Validation échoue si la quantité est null")
    void whenQuantityIsNull_ShouldHaveViolation() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Produit")
                .price(99.99)
                .quantity(null)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("La quantité est obligatoire");
    }

    @Test
    @DisplayName("Validation échoue si la quantité est négative")
    void whenQuantityIsNegative_ShouldHaveViolation() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Produit")
                .price(99.99)
                .quantity(-5)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("La quantité doit être positive ou zéro");
    }

    @Test
    @DisplayName("Validation réussie si la quantité est zéro")
    void whenQuantityIsZero_ShouldHaveNoViolation() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Produit")
                .price(99.99)
                .quantity(0)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Description est optionnelle")
    void whenDescriptionIsNull_ShouldHaveNoViolation() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Produit")
                .description(null)
                .price(99.99)
                .quantity(10)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Validation échoue avec plusieurs erreurs")
    void whenMultipleFieldsInvalid_ShouldHaveMultipleViolations() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("")
                .price(-10.00)
                .quantity(-5)
                .build();

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(3);
    }
}
