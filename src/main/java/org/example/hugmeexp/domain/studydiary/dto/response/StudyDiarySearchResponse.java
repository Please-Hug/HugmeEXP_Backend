package org.example.hugmeexp.domain.studydiary.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class StudyDiarySearchResponse {
    
    private Long id;
    private String name;
    private String title;
    private String contentPreview;  // content의 일부만 저장
    private int likeNum;
    private Long commentNum;  // COUNT 쿼리 결과는 Long 타입
    private LocalDateTime createdAt;
    
    // JPQL 생성자를 위한 커스텀 생성자
    public StudyDiarySearchResponse(Long id, String name, String title, 
                                  String contentPreview, int likeNum, 
                                  Long commentNum, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.contentPreview = contentPreview;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
        this.createdAt = createdAt;
    }
}