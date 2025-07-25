-- 현재 시간 설정
SET @now = NOW();

-- 가장 최근에 삽입된 스터디홀 ID들 가져오기 (LAST_INSERT_ID 사용)
-- 12개가 삽입되었으므로 마지막 ID부터 역순으로 12개를 가져옴
SET @lastId = LAST_INSERT_ID();

-- 각 스터디홀 ID 설정 (추정)
SET @hall1 = @lastId - 11;  -- 첫 번째 삽입된 ID
SET @hall2 = @lastId - 10;
SET @hall3 = @lastId - 9;
SET @hall4 = @lastId - 8;
SET @hall5 = @lastId - 7;
SET @hall6 = @lastId - 6;
SET @hall7 = @lastId - 5;
SET @hall8 = @lastId - 4;
SET @hall9 = @lastId - 3;
SET @hall10 = @lastId - 2;
SET @hall11 = @lastId - 1;
SET @hall12 = @lastId;      -- 마지막 삽입된 ID

-- 스터디룸 데이터 삽입 (기존에 삽입된 36개는 무시하고 새로 삽입)
INSERT INTO study_room (name, max_num, studyhall_id, created_at, modified_at) VALUES
-- 강남역 스터디 카페
('개인실 A', 1, @hall1, @now, @now),
('개인실 B', 1, @hall1, @now, @now),
('그룹실 1', 4, @hall1, @now, @now),

-- 역삼 학습공간
('프리미엄실 A', 2, @hall2, @now, @now),
('프리미엄실 B', 2, @hall2, @now, @now),
('회의실', 6, @hall2, @now, @now),

-- 홍대 스터디룸
('1인실 A', 1, @hall3, @now, @now),
('1인실 B', 1, @hall3, @now, @now),
('2인실', 2, @hall3, @now, @now),

-- 합정 스터디센터
('신규실 A', 1, @hall4, @now, @now),
('신규실 B', 1, @hall4, @now, @now),
('신규실 C', 3, @hall4, @now, @now),

-- 신촌 학습공간
('개인석 A', 1, @hall5, @now, @now),
('개인석 B', 1, @hall5, @now, @now),
('스터디룸', 4, @hall5, @now, @now),

-- 이태원 스터디홀
('럭셔리실 A', 1, @hall6, @now, @now),
('럭셔리실 B', 2, @hall6, @now, @now),
('VIP룸', 8, @hall6, @now, @now),

-- 건대 스터디센터
('학생실 A', 1, @hall7, @now, @now),
('학생실 B', 1, @hall7, @now, @now),
('그룹스터디실', 6, @hall7, @now, @now),

-- 구의 스터디룸
('조용한실 A', 1, @hall8, @now, @now),
('조용한실 B', 1, @hall8, @now, @now),
('프라이빗실', 2, @hall8, @now, @now),

-- 잠실 스터디카페
('최신실 A', 1, @hall9, @now, @now),
('최신실 B', 1, @hall9, @now, @now),
('프리미엄룸', 4, @hall9, @now, @now),

-- 노원 학습센터
('저렴한실 A', 1, @hall10, @now, @now),
('저렴한실 B', 1, @hall10, @now, @now),
('경제형실', 2, @hall10, @now, @now),

-- 분당 스터디카페
('신도시실 A', 1, @hall11, @now, @now),
('신도시실 B', 1, @hall11, @now, @now),
('패밀리룸', 5, @hall11, @now, @now),

-- 일산 학습공간
('넓은실 A', 1, @hall12, @now, @now),
('넓은실 B', 2, @hall12, @now, @now),
('대형회의실', 10, @hall12, @now, @now);

-- 결과 확인
SELECT '스터디룸 데이터 삽입 완료!' as 'STATUS';
SELECT COUNT(*) as '총 스터디홀 수' FROM study_hall;
SELECT COUNT(*) as '총 스터디룸 수' FROM study_room;