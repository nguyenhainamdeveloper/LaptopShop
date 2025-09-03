package com.nguyehainam.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nguyehainam.laptopshop.domain.Product;
import com.nguyehainam.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product pr) {
        return this.productRepository.save(pr);
    }

    public List<Product> fetchProducts() {
        return this.productRepository.findAll();
    }

    public Optional<Product> fetchProductById(long id) {
        return this.productRepository.findById(id);
    }

    public void deleteProduct(long id) {
        if (!this.productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id  = " + id);
        }
        this.productRepository.deleteById(id);
    }
}
