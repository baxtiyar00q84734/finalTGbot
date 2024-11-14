package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
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

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<Product> getAllAvailableProducts() {
        return productRepository.findAll();
    }
}
