-- =================================================================
-- 스터디홀 더미 데이터 삽입 (정확한 테이블 구조 기반)
-- =================================================================

-- 현재 시간 설정
SET @now = NOW();

-- 스터디홀 데이터 삽입 (정확한 컬럼명 사용)
INSERT INTO study_hall (
    name,
    description,
    simple_address,
    address,
    latitude,
    longitude,
    thumbnail,
    open_time,
    close_time,
    created_at,
    modified_at
) VALUES
      ('강남역 스터디 카페', '조용하고 집중하기 좋은 최상의 환경을 제공합니다. 24시간 운영합니다.', '강남역 1번 출구', '서울 강남구 강남대로 396', 37.4979, 127.0276, 'https://example.com/thumbnails/gangnam.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('역삼 학습공간', '역삼역 도보 3분 거리의 프리미엄 스터디룸입니다.', '역삼역 2번 출구', '서울 강남구 테헤란로 152', 37.5007, 127.0366, 'https://example.com/thumbnails/yeoksam.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('홍대 스터디룸', '홍대입구역 도보 5분, 젊은 분위기의 스터디 공간입니다.', '홍대입구역 9번 출구', '서울 마포구 와우산로 94', 37.5563, 126.9245, 'https://example.com/thumbnails/hongdae.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('합정 스터디센터', '합정역 근처 신규 오픈한 깨끗한 스터디룸입니다.', '합정역 3번 출구', '서울 마포구 합정동 369-5', 37.5496, 126.9140, 'https://example.com/thumbnails/hapjeong.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('신촌 학습공간', '신촌역 바로 앞 24시간 운영 스터디카페입니다.', '신촌역 1번 출구', '서울 서대문구 신촌로 83', 37.5559, 126.9364, 'https://example.com/thumbnails/sinchon.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('이태원 스터디홀', '이태원역 근처 국제적 분위기의 고급 스터디룸입니다.', '이태원역 4번 출구', '서울 용산구 이태원로 200', 37.5345, 126.9947, 'https://example.com/thumbnails/itaewon.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('건대 스터디센터', '건국대학교 근처 학생 전용 할인이 있는 스터디카페입니다.', '건대입구역 2번 출구', '서울 광진구 능동로 120', 37.5424, 127.0707, 'https://example.com/thumbnails/konkuk.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('구의 스터디룸', '구의역 근처 조용한 주거지역의 프라이빗 스터디룸입니다.', '구의역 1번 출구', '서울 광진구 구의동 546-1', 37.5487, 127.0857, 'https://example.com/thumbnails/guui.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('잠실 스터디카페', '잠실역 롯데월드몰 근처 최신식 스터디 공간입니다.', '잠실역 3번 출구', '서울 송파구 올림픽로 300', 37.5133, 127.1003, 'https://example.com/thumbnails/jamsil.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('노원 학습센터', '노원역 근처 저렴한 가격의 스터디룸입니다.', '노원역 1번 출구', '서울 노원구 상계로 380', 37.6542, 127.0615, 'https://example.com/thumbnails/nowon.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('분당 스터디카페', '분당선 서현역 근처 신도시 스터디룸입니다.', '서현역 1번 출구', '경기 성남시 분당구 서현로 180', 37.3838, 127.1230, 'https://example.com/thumbnails/bundang.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now),
      ('일산 학습공간', '일산서구청 근처 넓고 쾌적한 스터디 공간입니다.', '주엽역 2번 출구', '경기 고양시 일산서구 중앙로 1271', 37.6959, 126.7697, 'https://example.com/thumbnails/ilsan.jpg', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_ADD(@now, INTERVAL 19 HOUR), @now, @now);

-- 최근 삽입된 스터디홀들의 ID 조회 (정확한 기본키 컬럼명 사용)
SET @hall1 = (SELECT studyhall_id FROM study_hall WHERE name = '강남역 스터디 카페');
SET @hall2 = (SELECT studyhall_id FROM study_hall WHERE name = '역삼 학습공간');
SET @hall3 = (SELECT studyhall_id FROM study_hall WHERE name = '홍대 스터디룸');
SET @hall4 = (SELECT studyhall_id FROM study_hall WHERE name = '합정 스터디센터');
SET @hall5 = (SELECT studyhall_id FROM study_hall WHERE name = '신촌 학습공간');
SET @hall6 = (SELECT studyhall_id FROM study_hall WHERE name = '이태원 스터디홀');
SET @hall7 = (SELECT studyhall_id FROM study_hall WHERE name = '건대 스터디센터');
SET @hall8 = (SELECT studyhall_id FROM study_hall WHERE name = '구의 스터디룸');
SET @hall9 = (SELECT studyhall_id FROM study_hall WHERE name = '잠실 스터디카페');
SET @hall10 = (SELECT studyhall_id FROM study_hall WHERE name = '노원 학습센터');
SET @hall11 = (SELECT studyhall_id FROM study_hall WHERE name = '분당 스터디카페');
SET @hall12 = (SELECT studyhall_id FROM study_hall WHERE name = '일산 학습공간');

-- 스터디룸 데이터 삽입 (정확한 컬럼명 사용)
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
SELECT '🎉 스터디홀 및 스터디룸 데이터 삽입 완료!' as 'STATUS';
SELECT COUNT(*) as '총 스터디홀 수' FROM study_hall;
SELECT COUNT(*) as '총 스터디룸 수' FROM study_room;

-- 새로 삽입된 데이터 확인 (정확한 컬럼명으로 JOIN)
SELECT
    sh.name as '스터디홀명',
    COUNT(sr.studyroom_id) as '룸 개수',
    sh.simple_address as '위치',
    ROUND(sh.latitude, 4) as '위도',
    ROUND(sh.longitude, 4) as '경도'
FROM study_hall sh
         LEFT JOIN study_room sr ON sh.studyhall_id = sr.studyhall_id
WHERE sh.name IN (
                  '강남역 스터디 카페', '역삼 학습공간', '홍대 스터디룸', '합정 스터디센터',
                  '신촌 학습공간', '이태원 스터디홀', '건대 스터디센터', '구의 스터디룸',
                  '잠실 스터디카페', '노원 학습센터', '분당 스터디카페', '일산 학습공간'
    )
GROUP BY sh.studyhall_id, sh.name, sh.simple_address, sh.latitude, sh.longitude
ORDER BY sh.studyhall_id;