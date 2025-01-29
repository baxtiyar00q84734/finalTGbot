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
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = modelMapper.map(productRequestDTO, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        return modelMapper.map(product, ProductResponseDTO.class);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
        modelMapper.map(productRequestDTO, product);
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductResponseDTO.class);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product getProductByName(String name) {
        return productRepository.findByName(name).orElse(null);
    }
}
