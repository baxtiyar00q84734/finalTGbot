package org.example.finaltgbot.controller;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.request.CompanyRequestDTO;
import org.example.finaltgbot.dto.response.CompanyResponseDTO;
import org.example.finaltgbot.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@RequestBody CompanyRequestDTO companyRequestDTO) {
        CompanyResponseDTO createdCompany = companyService.createCompany(companyRequestDTO);
        return ResponseEntity.status(201).body(createdCompany);
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        List<CompanyResponseDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable Long id) {
        CompanyResponseDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(@PathVariable Long id, @RequestBody CompanyRequestDTO companyRequestDTO) {
        CompanyResponseDTO updatedCompany = companyService.updateCompany(id, companyRequestDTO);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

}
