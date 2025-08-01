package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.entity.TechItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TechItemRepository extends JpaRepository<TechItem, Long> {
    List<TechItem> findAllByEnglishNameIn(Collection<String> englishNames);
}
