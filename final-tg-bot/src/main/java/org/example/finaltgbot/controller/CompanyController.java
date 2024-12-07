package org.example.finaltgbot.controller;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.request.CompanyRequestDTO;
import org.example.finaltgbot.dto.response.CompanyResponseDTO;
import org.example.finaltgbot.service.CompanyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public CompanyResponseDTO createCompany(@RequestBody CompanyRequestDTO companyRequestDTO) {
        return companyService.createCompany(companyRequestDTO);
    }

    @GetMapping
    public List<CompanyResponseDTO> getAllCompanies() {
        return companyService.getAllCompanies();
    }
}
