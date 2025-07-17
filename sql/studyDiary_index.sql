-- Study Diary 검색 성능 개선을 위한 인덱스 추가

-- createdAt 인덱스 (정렬 성능 향상)
CREATE INDEX idx_study_diary_created_at ON study_diary(created_at);

-- like 쿼리 개선 인덱스
ALTER TABLE study_diary
ADD FULLTEXT INDEX ft_index_title_content (title, content);