package com.isi.devops.services;

import com.isi.devops.dto.request.ProductRequest;
import com.isi.devops.dto.response.ProductResponse;
import com.isi.devops.mapper.ProductMapper;
import com.isi.devops.model.Product;
import com.isi.devops.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour ProductServiceImpl")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Laptop de haute qualité")
                .price(999.99)
                .quantity(10)
                .build();

        productRequest = ProductRequest.builder()
                .name("Laptop")
                .description("Laptop de haute qualité")
                .price(999.99)
                .quantity(10)
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .description("Laptop de haute qualité")
                .price(999.99)
                .quantity(10)
                .build();
    }

    @Test
    @DisplayName("getAllProducts - Retourne tous les produits")
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(product);
        List<ProductResponse> expectedResponses = Arrays.asList(productResponse);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponseList(products)).thenReturn(expectedResponses);

        // Act
        List<ProductResponse> result = productService.getAllProducts();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toResponseList(products);
    }

    @Test
    @DisplayName("getProductById - Retourne le produit quand il existe")
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // Act
        ProductResponse result = productService.getProductById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getProductById - Lance une exception quand le produit n'existe pas")
    void getProductById_WhenProductNotExists_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Produit non trouvé");
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("createProduct - Crée et retourne le nouveau produit")
    void createProduct_ShouldCreateAndReturnProduct() {
        // Arrange
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        // Act
        ProductResponse result = productService.createProduct(productRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getPrice()).isEqualTo(999.99);
        verify(productMapper, times(1)).toEntity(productRequest);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct - Met à jour et retourne le produit")
    void updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() {
        // Arrange
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Laptop Pro")
                .description("Laptop amélioré")
                .price(1299.99)
                .quantity(5)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Laptop Pro")
                .description("Laptop amélioré")
                .price(1299.99)
                .quantity(5)
                .build();

        ProductResponse updatedResponse = ProductResponse.builder()
                .id(1L)
                .name("Laptop Pro")
                .description("Laptop amélioré")
                .price(1299.99)
                .quantity(5)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productMapper).updateEntityFromRequest(updateRequest, product);
        when(productRepository.save(product)).thenReturn(updatedProduct);
        when(productMapper.toResponse(updatedProduct)).thenReturn(updatedResponse);

        // Act
        ProductResponse result = productService.updateProduct(1L, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop Pro");
        assertThat(result.getPrice()).isEqualTo(1299.99);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("updateProduct - Lance une exception si le produit n'existe pas")
    void updateProduct_WhenProductNotExists_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(99L, productRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Produit non trouvé");
    }

    @Test
    @DisplayName("deleteProduct - Supprime le produit quand il existe")
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProduct - Lance une exception si le produit n'existe pas")
    void deleteProduct_WhenProductNotExists_ShouldThrowException() {
        // Arrange
        when(productRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Produit non trouvé");
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("searchProductsByName - Retourne les produits correspondants")
    void searchProductsByName_ShouldReturnMatchingProducts() {
        // Arrange
        List<Product> products = Arrays.asList(product);
        List<ProductResponse> expectedResponses = Arrays.asList(productResponse);

        when(productRepository.findByNameContainingIgnoreCase("Lap")).thenReturn(products);
        when(productMapper.toResponseList(products)).thenReturn(expectedResponses);

        // Act
        List<ProductResponse> result = productService.searchProductsByName("Lap");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("Lap");
    }
}
