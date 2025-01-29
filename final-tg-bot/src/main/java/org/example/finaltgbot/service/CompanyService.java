package org.example.finaltgbot.service;

import lombok.RequiredArgsConstructor;
import org.example.finaltgbot.dto.request.CompanyRequestDTO;
import org.example.finaltgbot.dto.response.CompanyResponseDTO;
import org.example.finaltgbot.entity.Company;
import org.example.finaltgbot.repository.CompanyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    public CompanyResponseDTO createCompany(CompanyRequestDTO companyRequestDTO) {
        Company company = modelMapper.map(companyRequestDTO, Company.class);
        Company savedCompany = companyRepository.save(company);
        return modelMapper.map(savedCompany, CompanyResponseDTO.class);
    }

    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(company -> modelMapper.map(company, CompanyResponseDTO.class))
                .collect(Collectors.toList());
    }

    public CompanyResponseDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + id));
        return modelMapper.map(company, CompanyResponseDTO.class);
    }

    public CompanyResponseDTO updateCompany(Long id, CompanyRequestDTO companyRequestDTO) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + id));
        modelMapper.map(companyRequestDTO, company);
        Company updatedCompany = companyRepository.save(company);
        return modelMapper.map(updatedCompany, CompanyResponseDTO.class);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}
