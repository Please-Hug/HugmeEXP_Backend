package org.example.hugmeexp.domain.recruitment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tech_stack", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"recruitment_id", "tech_item_id"})
})
public class TechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공고 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment;

    // 기술스택 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_item_id")
    private TechItem techItem;
}
