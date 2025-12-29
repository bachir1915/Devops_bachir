package com.isi.devops.controller;

import com.isi.devops.dto.request.ProductRequest;
import com.isi.devops.dto.response.ProductResponse;
import com.isi.devops.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Tests unitaires pour ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductResponse productResponse;
    private List<ProductResponse> productList;

    @BeforeEach
    void setUp() {
        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .description("Laptop de haute qualité")
                .price(999.99)
                .quantity(10)
                .build();

        productList = Arrays.asList(productResponse);
    }

    @Test
    @DisplayName("GET /products - Affiche la liste des produits")
    void listProducts_ShouldReturnProductsList() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", hasSize(1)));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /products/new - Affiche le formulaire de création")
    void showCreateForm_ShouldReturnFormView() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeExists("productRequest"));
    }

    @Test
    @DisplayName("POST /products - Crée un nouveau produit avec données valides")
    void createProduct_WithValidData_ShouldRedirectToList() throws Exception {
        // Arrange
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(productResponse);

        // Act & Assert
        mockMvc.perform(post("/products")
                        .param("name", "Laptop")
                        .param("description", "Description")
                        .param("price", "999.99")
                        .param("quantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("POST /products - Retourne erreurs avec données invalides")
    void createProduct_WithInvalidData_ShouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post("/products")
                        .param("name", "")  // Nom vide = invalide
                        .param("price", "-10")  // Prix négatif = invalide
                        .param("quantity", "-5"))  // Quantité négative = invalide
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().hasErrors());

        verify(productService, never()).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("GET /products/{id} - Affiche les détails d'un produit")
    void viewProduct_ShouldReturnProductDetails() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(productResponse);

        // Act & Assert
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/view"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", hasProperty("name", is("Laptop"))));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /products/{id}/edit - Affiche le formulaire d'édition")
    void showEditForm_ShouldReturnFormWithProduct() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(productResponse);

        // Act & Assert
        mockMvc.perform(get("/products/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeExists("productRequest"))
                .andExpect(model().attributeExists("productId"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("POST /products/{id} - Met à jour un produit avec données valides")
    void updateProduct_WithValidData_ShouldRedirectToList() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(productResponse);

        // Act & Assert
        mockMvc.perform(post("/products/1")
                        .param("name", "Laptop Pro")
                        .param("description", "Description mise à jour")
                        .param("price", "1299.99")
                        .param("quantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    @DisplayName("GET /products/{id}/delete - Supprime un produit")
    void deleteProduct_ShouldRedirectToList() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(get("/products/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("GET /products/search - Recherche des produits par nom")
    void searchProducts_WithName_ShouldReturnFilteredList() throws Exception {
        // Arrange
        when(productService.searchProductsByName("Lap")).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/products/search")
                        .param("name", "Lap"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("searchName"));

        verify(productService, times(1)).searchProductsByName("Lap");
    }

    @Test
    @DisplayName("GET /products/search - Sans nom retourne tous les produits")
    void searchProducts_WithoutName_ShouldReturnAllProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/products/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"));

        verify(productService, times(1)).getAllProducts();
    }
}
