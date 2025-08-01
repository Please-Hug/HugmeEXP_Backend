package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.entity.TagItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface TagItemRepository extends JpaRepository<TagItem, Long> {
    List<TagItem> findAllByTagNameIn(Set<String> tagNames);
}
