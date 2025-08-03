package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
