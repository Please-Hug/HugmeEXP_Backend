package org.example.hugmeexp.domain.recruitment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "tech_item")
public class TechItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String englishName;
    @Column(nullable = false)
    private String koreanName;
    @Column(nullable = false)
    private String iconUrl;
}
