package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
}
