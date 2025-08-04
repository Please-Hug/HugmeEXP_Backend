package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.entity.TechItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TechItemRepository extends JpaRepository<TechItem, Long> {
    List<TechItem> findAllByEnglishNameIn(Collection<String> englishNames);
}
