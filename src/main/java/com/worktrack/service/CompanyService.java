package com.worktrack.service;
import com.worktrack.model.Company;
import com.worktrack.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company findCompanyById(Long id) {
        Optional<Company> company = companyRepository.findById(id);

        if(company.isEmpty()){
            throw new RuntimeException("Company not found");
        }
        return company.get();
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Company updateCompany(Long id, Company company) {
        Optional<Company> dbcompany = companyRepository.findById(id);

        if(dbcompany.isEmpty()){
            throw new RuntimeException("Company not found");
        }
        Company existingCompany = dbcompany.get();

        existingCompany.setName(company.getName());
        existingCompany.setAddress(company.getAddress());
        existingCompany.setRegistrationNumber(company.getRegistrationNumber());
        existingCompany.setEmail(company.getEmail());

        return companyRepository.save(existingCompany);
    }

    public void deleteCompany(Long id) {
        Optional<Company> dbcompany = companyRepository.findById(id);

        if(dbcompany.isEmpty()){
            throw new RuntimeException("Company not found");
        }
        companyRepository.delete(dbcompany.get());
    }
}
