-- 안전한 초기 데이터 삽입 SQL 파일
-- 외래키 제약조건을 피하기 위해 실제 ID를 조회하여 사용

-- 1. 기존 데이터 정리 (필요시)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE orders;
TRUNCATE TABLE praise_receiver;
TRUNCATE TABLE praise;
TRUNCATE TABLE study_diary;
TRUNCATE TABLE bookmark;
TRUNCATE TABLE attendance;
TRUNCATE TABLE product;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. AUTO_INCREMENT 초기화
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE product AUTO_INCREMENT = 1;
ALTER TABLE praise AUTO_INCREMENT = 1;
ALTER TABLE attendance AUTO_INCREMENT = 1;
ALTER TABLE bookmark AUTO_INCREMENT = 1;
ALTER TABLE orders AUTO_INCREMENT = 1;
ALTER TABLE study_diary AUTO_INCREMENT = 1;
ALTER TABLE praise_receiver AUTO_INCREMENT = 1;

-- 3. 사용자 테이블 초기 데이터 (50명)
INSERT INTO users (username, password, name, role, point, exp, description, phone_number, created_at, modified_at) VALUES
('choihyun', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '최현', 'USER', 45, 5000, NULL, '010-4567-8901', '2025-06-21', NOW()),
('jungsooyoung', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '정수영', 'USER', 670, 380, '알고리즘 문제 풀이에 빠져있습니다.', '010-5678-9012', '2025-03-21', NOW()),
('kangdohyeon', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '강도현', 'USER', 290, 150, '데이터베이스 공부 중입니다.', '010-6789-0123', '2025-04-21', NOW()),
('leejaewon', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '이재원', 'ADMIN', 950, 480, '관리자입니다.', '010-7890-1234', '2025-06-22', NOW()),
('kimsoyeon', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '김소연', 'USER', 120, 65, 'React 공부 시작했어요!', '010-8901-2345', '2025-06-11', NOW()),
('parktaehyun', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '박태현', 'USER', 410, 220, NULL, '010-9012-3456', NOW(), NOW()),
('choiyuna', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '최유나', 'USER', 580, 310, 'Spring Boot 개발자 희망!', '010-0123-4567', NOW(), NOW()),
('jangminho', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '장민호', 'LECTURER', 720, 390, 'Python 강사입니다.', '010-1357-2468', NOW(), NOW()),
('yoonseulgi', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '윤슬기', 'USER', 180, 95, '신입 개발자입니다.', '010-2468-1357', NOW(), NOW()),
('seokyungmin', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '서경민', 'USER', 340, 170, NULL, '010-3579-2460', NOW(), NOW()),
('limjiwoo', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '임지우', 'USER', 450, 240, '웹 개발 공부 중입니다.', '010-4680-1379', NOW(), NOW()),
('hongminseok', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '홍민석', 'USER', 200, 110, '코딩 테스트 준비 중!', '010-5791-2468', NOW(), NOW()),
('baekjiyeon', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '백지연', 'USER', 620, 350, NULL, '010-6802-3571', NOW(), NOW()),
('ohseunghwan', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '오승환', 'USER', 380, 200, 'DevOps에 관심이 많습니다.', '010-7913-4682', NOW(), NOW()),
('songminji', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '송민지', 'USER', 90, 45, '초보 개발자예요.', '010-8024-5793', NOW(), NOW()),
('kwonhyukjin', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '권혁진', 'LECTURER', 780, 410, 'JavaScript 전문 강사입니다.', '010-9135-6804', NOW(), NOW()),
('hansomin', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '한소민', 'USER', 520, 280, 'UI/UX 디자인도 공부해요.', '010-0246-7915', NOW(), NOW()),
('yoosangwook', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '유상욱', 'USER', 260, 135, NULL, '010-1358-8026', NOW(), NOW()),
('leeseungbin', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '이승빈', 'USER', 490, 260, '모바일 앱 개발 공부 중!', '010-2469-9137', NOW(), NOW()),
('parkjinsoo', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '박진수', 'USER', 160, 85, '게임 개발자가 되고 싶어요.', '010-3570-0248', NOW(), NOW()),
('kimdahye', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '김다혜', 'USER', 710, 370, NULL, '010-4681-1359', NOW(), NOW()),
('choijunho', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '최준호', 'USER', 350, 180, '백엔드 API 개발 전문가 목표!', '010-5792-2460', NOW(), NOW()),
('jeonghaeun', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '정하은', 'USER', 130, 70, '데이터 사이언스 공부해요.', '010-6803-3571', NOW(), NOW()),
('shindonghyuk', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '신동혁', 'USER', 430, 230, NULL, '010-7914-4682', NOW(), NOW()),
('kimeunbi', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '김은비', 'USER', 560, 300, '풀스택 개발자 꿈꿔요!', '010-8025-5793', NOW(), NOW()),
('leekwangsoo', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '이광수', 'USER', 80, 40, '프로그래밍 초보입니다.', '010-9136-6804', NOW(), NOW()),
('parksooyoung', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '박수영', 'USER', 640, 340, NULL, '010-0247-7915', NOW(), NOW()),
('choihyeseong', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '최혜성', 'LECTURER', 890, 450, 'AI/ML 강사입니다.', '010-1359-8026', NOW(), NOW()),
('jangeunji', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '장은지', 'USER', 270, 140, '블록체인 기술에 관심 많아요.', '010-2460-9137', NOW(), NOW()),
('yoonchanhee', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '윤찬희', 'USER', 400, 210, NULL, '010-3571-0248', NOW(), NOW()),
('hanjiho', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '한지호', 'USER', 190, 100, '네트워크 보안 공부 중!', '010-4682-1359', NOW(), NOW()),
('leeminji', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '이민지', 'USER', 480, 250, '클라우드 컴퓨팅 전문가 희망!', '010-5793-2460', NOW(), NOW()),
('parkseungjun', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '박승준', 'USER', 320, 165, NULL, '010-6804-3571', NOW(), NOW()),
('kimjeongsoo', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '김정수', 'USER', 750, 380, '사이버보안 전문가가 될 거예요.', '010-7915-4682', NOW(), NOW()),
('choiyewon', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '최예원', 'USER', 110, 55, '웹 퍼블리셔 공부해요.', '010-8026-5793', NOW(), NOW()),
('jeonghyunjin', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '정현진', 'USER', 540, 290, NULL, '010-9137-6804', NOW(), NOW()),
('yoojaehwan', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '유재환', 'USER', 360, 190, '임베디드 시스템 개발자 목표!', '010-0248-7915', NOW(), NOW()),
('kangjisu', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '강지수', 'USER', 220, 120, '데이터 분석가가 되고 싶어요.', '010-1360-8026', NOW(), NOW()),
('limseoyoung', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '임서영', 'USER', 600, 320, NULL, '010-2471-9137', NOW(), NOW()),
('ohdongjin', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '오동진', 'USER', 140, 75, '로봇 공학 공부 중입니다.', '010-3582-0248', NOW(), NOW()),
('songminhye', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '송민혜', 'USER', 470, 245, '소프트웨어 아키텍트 목표!', '010-4693-1359', NOW(), NOW()),
('kwonseungho', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '권승호', 'USER', 300, 155, NULL, '010-5704-2460', NOW(), NOW()),
('hanjimin', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '한지민', 'USER', 680, 360, '퀀트 개발자가 되고 싶어요!', '010-6815-3571', NOW(), NOW()),
('leejunwoo', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '이준우', 'USER', 240, 125, '게임 서버 개발자 희망!', '010-7926-4682', NOW(), NOW()),
('parkdayeon', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '박다연', 'USER', 510, 270, NULL, '010-8037-5793', NOW(), NOW()),
('kimseongmin', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '김성민', 'USER', 170, 90, 'VR/AR 개발에 관심이 많아요.', '010-9148-6804', NOW(), NOW()),
('choijeonghoon', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '최정훈', 'USER', 420, 215, '핀테크 개발자가 될 거예요!', '010-0259-7915', NOW(), NOW()),
('junghyeji', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '정혜지', 'USER', 590, 315, NULL, '010-1360-8027', NOW(), NOW()),
('shinara', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '신아라', 'USER', 310, 160, 'UI 개발에 관심이 많습니다.', '010-9159-8026', NOW(), NOW()),
('leejihwan', '$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m', '이지환', 'USER', 260, 140, '클린 아키텍처를 공부하고 있어요.', '010-0260-9137', NOW(), NOW()),
('admin01', '$2a$10$t2OmMOInDxTUKBbltzp5ceYc11MqcnthlAGHWTSF3anEIT1M6vBX6', '관리자', 'ADMIN', 1000, 500, '시스템 관리자입니다.', '010-1111-2222', NOW(), NOW());


-- 4. 출석 데이터 - 실제 존재하는 사용자 ID만 사용
INSERT INTO attendance (user_id, attendance_date, created_at, modified_at) 
SELECT u.id, '2025-02-21', NOW(), NOW() FROM users u WHERE u.id <= 3
UNION ALL
SELECT u.id, '2025-03-21', NOW(), NOW() FROM users u WHERE u.id IN (1, 4, 5)
UNION ALL
SELECT u.id, '2025-04-21', NOW(), NOW() FROM users u WHERE u.id IN (2, 6, 7)
UNION ALL
SELECT u.id, '2025-05-21', NOW(), NOW() FROM users u WHERE u.id IN (1, 3, 8)
UNION ALL
SELECT u.id, '2025-01-19', NOW(), NOW() FROM users u WHERE u.id IN (9, 10)
UNION ALL
SELECT u.id, '2025-07-21', NOW(), NOW() FROM users u WHERE u.id IN (2, 11)
UNION ALL
SELECT u.id, '2025-07-19', NOW(), NOW() FROM users u WHERE u.id IN (5, 12)
UNION ALL
SELECT u.id, '2025-07-20', NOW(), NOW() FROM users u WHERE u.id IN (13, 14);

-- 5. 북마크 데이터 - 실제 존재하는 사용자 ID만 사용
INSERT INTO bookmark (user_id, title, link, created_at, modified_at) 
SELECT u.id, 
       CASE u.id % 20
           WHEN 1 THEN 'Spring Boot 공식 문서'
           WHEN 2 THEN 'React 공식 가이드'
           WHEN 3 THEN 'Java 튜토리얼'
           WHEN 4 THEN 'MDN Web Docs'
           WHEN 5 THEN '백준 온라인 저지'
           WHEN 6 THEN 'GitHub'
           WHEN 7 THEN 'Stack Overflow'
           WHEN 8 THEN 'LeetCode'
           WHEN 9 THEN 'Programmers'
           WHEN 10 THEN 'Vue.js 공식 문서'
           WHEN 11 THEN 'Python 공식 문서'
           WHEN 12 THEN 'W3Schools'
           WHEN 13 THEN 'Node.js 공식 사이트'
           WHEN 14 THEN 'CodePen'
           WHEN 15 THEN 'MySQL 공식 문서'
           WHEN 16 THEN 'Docker 공식 문서'
           WHEN 17 THEN 'AWS 문서'
           WHEN 18 THEN '인프런'
           WHEN 19 THEN 'YouTube 생활코딩'
           ELSE 'Codecademy'
       END,
       CASE u.id % 20
           WHEN 1 THEN 'https://spring.io/projects/spring-boot'
           WHEN 2 THEN 'https://react.dev/'
           WHEN 3 THEN 'https://docs.oracle.com/javase/tutorial/'
           WHEN 4 THEN 'https://developer.mozilla.org/'
           WHEN 5 THEN 'https://www.acmicpc.net/'
           WHEN 6 THEN 'https://github.com/'
           WHEN 7 THEN 'https://stackoverflow.com/'
           WHEN 8 THEN 'https://leetcode.com/'
           WHEN 9 THEN 'https://school.programmers.co.kr/'
           WHEN 10 THEN 'https://vuejs.org/'
           WHEN 11 THEN 'https://docs.python.org/'
           WHEN 12 THEN 'https://www.w3schools.com/'
           WHEN 13 THEN 'https://nodejs.org/'
           WHEN 14 THEN 'https://codepen.io/'
           WHEN 15 THEN 'https://dev.mysql.com/doc/'
           WHEN 16 THEN 'https://docs.docker.com/'
           WHEN 17 THEN 'https://docs.aws.amazon.com/'
           WHEN 18 THEN 'https://www.inflearn.com/'
           WHEN 19 THEN 'https://www.youtube.com/user/egoing2'
           ELSE 'https://www.codecademy.com/'
       END,
       NOW(), NOW()
FROM users u WHERE u.id <= 20;

-- 6. 상품 테이블 초기 데이터 (독립적)
INSERT INTO product (name, brand, quantity, price, is_deleted) VALUES
('초코파이 12개입', '오리온', 50, 4500, false),
('바나나킥 75g', '농심', 30, 1800, false),
('컵라면 대용량', '농심', 25, 2200, false),
('아메리카노 원두 200g', '스타벅스', 15, 12000, false),
('허니버터칩 60g', '해태', 40, 2500, false),
('딸기우유 200ml', '서울우유', 60, 1200, false),
('새우깡 90g', '농심', 35, 1900, false),
('김밥천국 도시락', '김밥천국', 20, 8500, false),
('초코우유 500ml', '빙그레', 45, 2000, false),
('치킨너겟 냉동', 'CJ', 12, 7800, false),
('피자 냉동식품', '오뚜기', 8, 15000, false),
('라면 5개입', '오뚜기', 30, 4200, false),
('아이스크림 6개입', '롯데', 18, 9500, false),
('샌드위치', '파리바게뜨', 22, 5500, false),
('과자 선물세트', '해태', 10, 25000, false),
('에너지드링크 250ml', '레드불', 50, 3000, false),
('즉석밥 210g', 'CJ', 40, 1500, false),
('김치찌개 레토르트', '동원', 25, 3800, false),
('견과류 믹스 200g', '아몬드브리즈', 20, 8200, false),
('카페라떼 캔커피', '맥심', 60, 1800, false);

-- 7. 칭찬 데이터 - 실제 존재하는 사용자만 사용 (100개 생성)
INSERT INTO praise (sender_id, content, praise_type, created_at, modified_at)
SELECT 
    ((ROW_NUMBER() OVER() - 1) % 50) + 1 as sender_id,
    CASE ((ROW_NUMBER() OVER() - 1) % 50)
        WHEN 0 THEN '오늘 알고리즘 문제 해결하는 걸 보니 정말 대단해요!'
        WHEN 1 THEN '프로젝트에서 많은 도움을 주셔서 감사합니다.'
        WHEN 2 THEN '새로운 기술 도전하는 모습이 멋져요! 파이팅!'
        WHEN 3 THEN '코드 리뷰를 정말 꼼꼼히 해주셔서 감사해요.'
        WHEN 4 THEN '버그 수정하느라 고생하셨어요. 정말 대단해요!'
        WHEN 5 THEN '늦은 시간까지 개발하시는 모습 응원해요!'
        WHEN 6 THEN '깔끔한 코드 작성 실력이 인정됩니다!'
        WHEN 7 THEN '팀 프로젝트에서 많은 기여를 해주셔서 감사해요.'
        WHEN 8 THEN '새로운 프레임워크 학습하시는 열정이 대단해요!'
        WHEN 9 THEN '데이터베이스 설계를 잘 해주셔서 감사합니다.'
        WHEN 10 THEN 'API 문서 정리해주신 거 정말 도움이 됐어요!'
        WHEN 11 THEN '힘든 시기에도 포기하지 않고 계속 노력하세요!'
        WHEN 12 THEN 'Git 사용법 알려주셔서 정말 감사해요.'
        WHEN 13 THEN '테스트 코드 작성 실력이 정말 좋아요!'
        WHEN 14 THEN '새로운 도전 응원합니다! 화이팅!'
        WHEN 15 THEN '배포 과정에서 많은 도움을 주셔서 감사해요.'
        WHEN 16 THEN '복잡한 로직을 깔끔하게 정리하는 능력이 대단해요!'
        WHEN 17 THEN '스터디 그룹 운영해주셔서 감사합니다.'
        WHEN 18 THEN '어려운 개념도 쉽게 설명해주시는 능력이 인정돼요!'
        WHEN 19 THEN '포기하지 말고 끝까지 해내세요! 응원해요!'
        WHEN 20 THEN '코드 최적화 작업 고생하셨어요!'
        WHEN 21 THEN '새로운 기술 스택 도입하는 모습이 멋져요!'
        WHEN 22 THEN '밤늦게까지 개발하시느라 고생 많으세요. 화이팅!'
        WHEN 23 THEN '오류 해결해주셔서 정말 감사해요.'
        WHEN 24 THEN '창의적인 해결책을 제시하는 능력이 대단해요!'
        WHEN 25 THEN '새로운 언어 배우시는 열정 응원해요!'
        WHEN 26 THEN '프론트엔드 작업 도움 주셔서 감사합니다.'
        WHEN 27 THEN '백엔드 아키텍처 설계 실력이 인정돼요!'
        WHEN 28 THEN '힘들어도 포기하지 마세요! 꼭 해내실 거예요!'
        WHEN 29 THEN '퍼포먼스 튜닝 작업 감사해요.'
        WHEN 30 THEN '보안 이슈 해결해주셔서 정말 감사합니다!'
        WHEN 31 THEN '클린 코드 작성 스타일이 정말 좋아요!'
        WHEN 32 THEN '새로운 프로젝트 시작하는 것 응원해요!'
        WHEN 33 THEN 'CI/CD 파이프라인 구축해주셔서 감사해요.'
        WHEN 34 THEN '문제 해결 능력이 정말 뛰어나세요!'
        WHEN 35 THEN '코딩 테스트 준비하는 모습 응원합니다!'
        WHEN 36 THEN '좋은 라이브러리 추천해주셔서 감사해요.'
        WHEN 37 THEN '리팩토링 실력이 정말 인정됩니다!'
        WHEN 38 THEN '취업 준비하시는 것 화이팅해요!'
        WHEN 39 THEN '코드 리뷰에서 좋은 피드백 주셔서 감사해요.'
        WHEN 40 THEN '알고리즘 최적화 실력이 대단해요!'
        WHEN 41 THEN '새로운 도전 계속 응원할게요!'
        WHEN 42 THEN '버전 관리 잘 해주셔서 감사합니다.'
        WHEN 43 THEN '디자인 패턴 적용 능력이 뛰어나세요!'
        WHEN 44 THEN '개발자 스터디 모임 열심히 하세요!'
        WHEN 45 THEN '예외 처리 잘 해주셔서 감사해요.'
        WHEN 46 THEN '코드 가독성이 정말 좋아요!'
        WHEN 47 THEN '새로운 기술 도전하는 용기 응원해요!'
        WHEN 48 THEN '데이터 분석 작업 도움 주셔서 감사해요.'
        ELSE '문서화 능력이 정말 훌륭해요!'
    END,
    CASE ((ROW_NUMBER() OVER() - 1) % 3)
        WHEN 0 THEN 'THANKS'
        WHEN 1 THEN 'CHEER'
        ELSE 'RECOGNIZE'
    END,
    NOW(), NOW()
FROM (
    SELECT 1 as dummy FROM users LIMIT 2
) t1
CROSS JOIN (
    SELECT 1 as dummy FROM users LIMIT 50  
) t2;

-- 8. 칭찬수신자 데이터 - 실제 존재하는 praise_id와 user_id만 사용 (100개 생성)
INSERT INTO praise_receiver (praise_id, receiver_id, created_at, modified_at)
SELECT 
    ((ROW_NUMBER() OVER() - 1) % 100) + 1 as praise_id,
    ((ROW_NUMBER() OVER() - 1) % 50) + 1 as receiver_id,
    NOW(), NOW()
FROM (
    SELECT 1 as dummy FROM users LIMIT 2
) t1
CROSS JOIN (
    SELECT 1 as dummy FROM users LIMIT 50  
) t2;

-- 9. 주문 데이터 - 실제 존재하는 사용자와 상품만 사용
INSERT INTO orders (purchaser_id, product_id, receiver_phone_number, created_at, modified_at)
SELECT 
    u.id,
    p.id,
    u.phone_number,
    NOW(), NOW()
FROM users u, product p
WHERE u.id <= 20 AND p.id <= 20 AND (u.id + p.id) % 3 = 0
LIMIT 20;

-- 10. 공부일기 데이터 - 실제 존재하는 사용자만 사용 (100개 생성)
INSERT INTO study_diary (user_id, title, content, is_created, like_count, created_at, modified_at)
SELECT 
    ((ROW_NUMBER() OVER() - 1) % 50) + 1 as user_id,
    CASE ((ROW_NUMBER() OVER() - 1) % 50)
        WHEN 0 THEN 'Spring Boot 첫 프로젝트 시작'
        WHEN 1 THEN 'React Hook 정리'
        WHEN 2 THEN 'Java 스트림 API 활용'
        WHEN 3 THEN 'CSS Grid와 Flexbox 비교'
        WHEN 4 THEN '알고리즘 문제 해결 과정'
        WHEN 5 THEN 'MySQL 인덱스 최적화'
        WHEN 6 THEN 'Git 브랜치 전략 학습'
        WHEN 7 THEN 'TypeScript 타입 시스템'
        WHEN 8 THEN 'Docker 컨테이너 기초'
        WHEN 9 THEN 'REST API 설계 원칙'
        WHEN 10 THEN 'Python 데이터 분석 시작'
        WHEN 11 THEN 'JPA 연관관계 매핑'
        WHEN 12 THEN 'JavaScript 비동기 처리'
        WHEN 13 THEN 'Vue.js 컴포넌트 통신'
        WHEN 14 THEN 'AWS EC2 서버 배포'
        WHEN 15 THEN 'Redux 상태 관리'
        WHEN 16 THEN '코딩 테스트 문제 풀이'
        WHEN 17 THEN 'MongoDB NoSQL 기초'
        WHEN 18 THEN 'Node.js Express 서버'
        WHEN 19 THEN 'CSS 애니메이션 구현'
        WHEN 20 THEN 'Spring Security 인증'
        WHEN 21 THEN 'React Router 페이지 관리'
        WHEN 22 THEN '자료구조 스택과 큐'
        WHEN 23 THEN 'Webpack 모듈 번들링'
        WHEN 24 THEN 'SQL 쿼리 최적화 기법'
        WHEN 25 THEN 'GraphQL API 설계'
        WHEN 26 THEN 'Linux 명령어 정리'
        WHEN 27 THEN 'TDD 테스트 주도 개발'
        WHEN 28 THEN 'Sass CSS 전처리기'
        WHEN 29 THEN 'Firebase 실시간 데이터베이스'
        WHEN 30 THEN '객체지향 프로그래밍 원칙'
        WHEN 31 THEN 'HTTP 프로토콜 이해'
        WHEN 32 THEN '디자인 패턴 공부'
        WHEN 33 THEN 'Git 고급 기능 활용'
        WHEN 34 THEN '함수형 프로그래밍 개념'
        WHEN 35 THEN 'Redis 캐싱 전략'
        WHEN 36 THEN 'React Hooks 심화'
        WHEN 37 THEN 'API 설계 베스트 프랙티스'
        WHEN 38 THEN 'Docker Compose 활용'
        WHEN 39 THEN 'TypeScript 고급 타입'
        WHEN 40 THEN 'Vue 3 Composition API'
        WHEN 41 THEN '웹 성능 최적화 기법'
        WHEN 42 THEN 'GraphQL 스키마 설계'
        WHEN 43 THEN 'Kubernetes 기초'
        WHEN 44 THEN 'Next.js SSR 구현'
        WHEN 45 THEN 'Jest 단위 테스트'
        WHEN 46 THEN '알고리즘 시간복잡도'
        WHEN 47 THEN 'Express 미들웨어 개발'
        WHEN 48 THEN 'CSS Grid 고급 레이아웃'
        ELSE 'PWA 개발 기초'
    END,
    CASE ((ROW_NUMBER() OVER() - 1) % 25)
        WHEN 0 THEN '오늘부터 Spring Boot를 이용한 웹 개발 프로젝트를 시작했다. MVC 패턴과 의존성 주입에 대해 배웠고, 간단한 Controller를 만들어 Hello World를 출력해보았다. 처음에는 어려웠지만 차근차근 따라하니 이해가 되기 시작했다.'
        WHEN 1 THEN 'useState와 useEffect Hook에 대해 깊이 공부했다. 함수형 컴포넌트에서 상태 관리하는 방법과 생명주기를 다루는 방법을 익혔다. 특히 useEffect의 의존성 배열 개념이 중요하다는 것을 깨달았다.'
        WHEN 2 THEN '자바의 스트림 API를 이용해서 컬렉션 데이터를 처리하는 방법을 학습했다. map, filter, reduce 등의 메서드를 사용하여 함수형 프로그래밍 스타일로 코드를 작성해보았다. 코드가 훨씬 간결해졌다.'
        WHEN 3 THEN 'CSS 레이아웃을 위한 Grid와 Flexbox의 차이점을 정리했다. 1차원 레이아웃에는 Flexbox가, 2차원 레이아웃에는 Grid가 적합하다는 것을 실습을 통해 확인했다.'
        WHEN 4 THEN '오늘 백준에서 DP 문제를 풀었다. 처음에는 접근 방법을 찾지 못해 막막했지만, 작은 부분 문제로 나누어 생각하니 해결할 수 있었다. 점화식을 세우는 연습이 더 필요하다.'
        WHEN 5 THEN '데이터베이스 성능 향상을 위한 인덱스 사용법을 공부했다. 복합 인덱스와 단일 인덱스의 차이점, 그리고 실행 계획을 통해 쿼리 최적화하는 방법을 배웠다.'
        WHEN 6 THEN 'Git Flow와 GitHub Flow에 대해 비교 분석했다. 팀 프로젝트에서 효율적인 브랜치 관리 전략의 중요성을 깨달았다. 실제 프로젝트에 적용해보고 싶다.'
        WHEN 7 THEN 'TypeScript의 강력한 타입 시스템을 공부했다. 인터페이스, 제네릭, 유니온 타입 등을 활용하여 더 안전한 코드를 작성하는 방법을 익혔다.'
        WHEN 8 THEN 'Docker를 이용한 컨테이너화에 대해 배웠다. Dockerfile 작성법과 이미지 빌드 과정을 실습했다. 개발 환경 통일에 매우 유용할 것 같다.'
        WHEN 9 THEN 'RESTful API 설계 원칙과 HTTP 메서드 사용법을 정리했다. 리소스 중심의 URL 설계와 상태 코드 활용법을 배웠다.'
        WHEN 10 THEN 'Pandas와 NumPy를 이용한 데이터 분석 기초를 학습했다. CSV 파일을 읽어와 기본적인 통계 분석을 해보았다. 데이터 시각화도 배워보고 싶다.'
        WHEN 11 THEN 'JPA에서 엔티티 간의 연관관계 매핑을 공부했다. OneToMany, ManyToOne 등의 관계를 실제 코드로 구현해보며 이해를 높였다.'
        WHEN 12 THEN 'Promise와 async/await를 이용한 비동기 처리 방법을 깊이 있게 공부했다. 콜백 지옥을 해결하는 우아한 방법을 배웠다.'
        WHEN 13 THEN 'Vue.js에서 부모-자식 컴포넌트 간의 데이터 통신 방법을 학습했다. props와 emit을 활용한 양방향 통신을 실습했다.'
        WHEN 14 THEN '처음으로 AWS EC2에 웹 애플리케이션을 배포해보았다. 인스턴스 생성부터 도메인 연결까지 전 과정을 경험했다. 클라우드의 편리함을 실감했다.'
        WHEN 15 THEN 'React에서 Redux를 이용한 전역 상태 관리를 공부했다. 액션, 리듀서, 스토어의 개념과 데이터 플로우를 이해했다.'
        WHEN 16 THEN '프로그래머스에서 문자열 처리 문제를 풀었다. 정규표현식을 활용하여 효율적으로 해결할 수 있었다. 문제 해결 능력이 늘고 있는 것 같다.'
        WHEN 17 THEN 'NoSQL 데이터베이스인 MongoDB를 처음 다뤄보았다. 관계형 DB와는 다른 document 기반의 데이터 모델링을 배웠다.'
        WHEN 18 THEN 'Node.js와 Express를 이용해 간단한 웹 서버를 만들어보았다. 라우팅과 미들웨어 개념을 실습을 통해 익혔다.'
        WHEN 19 THEN 'CSS3 애니메이션과 transition을 이용해 동적인 웹 페이지를 만들어보았다. keyframes를 활용한 복잡한 애니메이션도 도전해봤다.'
        WHEN 20 THEN 'Spring Security를 이용한 사용자 인증 시스템을 구현했다. JWT 토큰 기반 인증 방식을 적용해보았다.'
        WHEN 21 THEN 'React Router를 이용한 SPA 페이지 라우팅을 공부했다. 동적 라우팅과 중첩 라우팅을 구현해보았다.'
        WHEN 22 THEN '기본 자료구조인 스택과 큐의 개념과 구현 방법을 복습했다. 각각의 특성과 활용 사례를 정리했다.'
        WHEN 23 THEN 'Webpack을 이용한 모듈 번들링에 대해 학습했다. entry, output, loader, plugin의 개념을 익혔다.'
        ELSE '복잡한 SQL 쿼리의 성능을 개선하는 방법을 공부했다. 인덱스 활용과 쿼리 재작성 기법을 배웠다.'
    END,
    true,
    ((ROW_NUMBER() OVER() - 1) % 20) + 1,
    NOW(), NOW()
FROM (
    SELECT 1 as dummy FROM users LIMIT 2
) t1
CROSS JOIN (
    SELECT 1 as dummy FROM users LIMIT 50  
) t2;