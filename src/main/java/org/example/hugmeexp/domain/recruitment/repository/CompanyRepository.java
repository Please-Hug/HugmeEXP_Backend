package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByCompanyNameContainingIgnoreCase(String companyName);
}
