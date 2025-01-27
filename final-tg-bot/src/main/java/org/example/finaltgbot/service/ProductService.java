package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.request.ProductRequestDTO;
import org.example.finaltgbot.dto.response.ProductResponseDTO;
import org.example.finaltgbot.entity.Product;
import org.example.finaltgbot.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final int MAX_DEPTH = 10;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private int currentDepth = 0;

    //CRUD

    public List<Product> getAllAvailableProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }



    public void createProduct(ProductRequestDTO productRequestDTO) {
        Product map = modelMapper.map(productRequestDTO, Product.class);
        productRepository.save(map);
    }

    public List<ProductResponseDTO> getAllProducts() {
        if (currentDepth > MAX_DEPTH) {
            throw new IllegalStateException("Exceeded maximum depth for recursion.");
        }

        try {
            currentDepth++;
            List<Product> products = productRepository.findAll();

            return products.stream()
                    .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                    .collect(Collectors.toList());
        } finally {
            currentDepth--;
        }
    }


    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product getProductByName(String name) {
        return productRepository.findByName(name).orElse(null); // Assuming you have a method in ProductRepository
    }
}
