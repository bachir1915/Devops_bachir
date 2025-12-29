package com.isi.devops.controller;

import com.isi.devops.dto.request.ProductRequest;
import com.isi.devops.dto.response.ProductResponse;
import com.isi.devops.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String listProducts(Model model) {
        List<ProductResponse> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("productRequest", new ProductRequest());
        return "products/form";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute("productRequest") ProductRequest request,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "products/form";
        }
        productService.createProduct(request);
        redirectAttributes.addFlashAttribute("successMessage", "Produit créé avec succès!");
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        ProductResponse product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "products/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductResponse product = productService.getProductById(id);
        ProductRequest request = ProductRequest.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
        model.addAttribute("productRequest", request);
        model.addAttribute("productId", id);
        return "products/form";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productRequest") ProductRequest request,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("productId", id);
            return "products/form";
        }
        productService.updateProduct(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Produit mis à jour avec succès!");
        return "redirect:/products";
    }

    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Produit supprimé avec succès!");
        return "redirect:/products";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam(required = false) String name, Model model) {
        List<ProductResponse> products;
        if (name != null && !name.trim().isEmpty()) {
            products = productService.searchProductsByName(name);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("searchName", name);
        return "products/list";
    }
}
