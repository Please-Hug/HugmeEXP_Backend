package org.example.hugmeexp.domain.studydiary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.notification.service.NotificationService;
import org.example.hugmeexp.domain.studydiary.dto.request.CommentCreateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryCreateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryUpdateRequest;
import org.example.hugmeexp.domain.studydiary.dto.response.CommentDetailResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryDetailResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryFindAllResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryMyHomeResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryWeekStatusResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiarySearchResponse;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiary;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryComment;
import org.example.hugmeexp.domain.studydiary.exception.StudyDiaryNotFoundException;
import org.example.hugmeexp.domain.studydiary.exception.UnauthorizedAccessException;
import org.example.hugmeexp.domain.studydiary.exception.UserNotFoundForStudyDiaryException;
import org.example.hugmeexp.domain.studydiary.exception.CommentNotFoundException;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryCommentRepository;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryRepository;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryLikeRepository;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryLike;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.enums.UserRole;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyDiaryService {

    private final UserRepository userRepository;
    private final StudyDiaryRepository studyDiaryRepository;
    private final StudyDiaryCommentRepository studyDiaryCommentRepository;
    private final NotificationService notificationService;

    @Transactional
    public Long createStudyDiary(StudyDiaryCreateRequest createRequest, UserDetails userDetails){
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary createdStudyDiary = StudyDiary.builder()
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .likeCount(0)
                .isCreated(true)
                .user(user)
                .build();
        StudyDiary saved = studyDiaryRepository.save(createdStudyDiary);

        return saved.getId();
    }

    @Transactional
    public Long updateStudyDiary(Long id, StudyDiaryUpdateRequest updateRequest, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary studyDiary = studyDiaryRepository.findById(id)
                .orElseThrow(StudyDiaryNotFoundException::new);

        checkUser(user, studyDiary);

        studyDiary.updateTitle(updateRequest.getTitle());
        studyDiary.updateContent(updateRequest.getContent());

        return studyDiary.getId();
    }

    @Transactional
    public void deleteStudyDiary(Long id, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary studyDiary = studyDiaryRepository.findById(id)
                .orElseThrow(StudyDiaryNotFoundException::new);

        try {
            checkUser(user, studyDiary);
        } catch (UnauthorizedAccessException e) {
            checkAdmin(user);
        }
        // 댓글 알림 삭제
        List<StudyDiaryComment> comments = studyDiaryCommentRepository.findByStudyDiary(studyDiary);
        for(StudyDiaryComment comment : comments){
            // 알림 제거 추가
            notificationService.deleteDiaryCommentNotification(user, comment.getId());
        }

        // 알림 제거 추가
        notificationService.deleteAllByDiaryId(studyDiary.getUser(),studyDiary.getId());

        studyDiaryRepository.delete(studyDiary);
    }

    public Page<StudyDiaryFindAllResponse> getStudyDiaries(Pageable pageable) {
        Page<Object[]> studyDiaries = studyDiaryRepository.findByIsCreatedTrueOrderByCreatedAtDesc(pageable);

        //response로 전환
        Page<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = studyDiaries.map(result -> {
            Long id = (Long) result[0];
            User user = (User) result[1];
            String title = (String) result[2];
            String content = (String) result[3];
            Integer likeCount = (Integer) result[4];
            Long commentCount = (Long) result[5];
            LocalDateTime createdAt = (LocalDateTime) result[6];
            
            return StudyDiaryFindAllResponse.builder()
                    .id(id)
                    .name(user.getName())
                    .title(title)
                    .content(content)
                    .likeNum(likeCount)
                    .commentNum(commentCount.intValue())
                    .createdAt(createdAt)
                    .build();
        });

        return studyDiaryFindAllResponsePage;
    }

    public Page<StudyDiaryFindAllResponse> getTodayPopularStudyDiaries(Pageable pageable) {
        // 오늘의 시작과 끝 시간 계산
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Page<Object[]> studyDiaries = studyDiaryRepository.findTodayPopularStudyDiaries(startOfDay, endOfDay, pageable);

        //response로 전환
        Page<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = studyDiaries.map(result -> {
            Long id = (Long) result[0];
            User user = (User) result[1];
            String title = (String) result[2];
            String content = (String) result[3];
            Integer likeCount = (Integer) result[4];
            Long commentCount = (Long) result[5];
            LocalDateTime createdAt = (LocalDateTime) result[6];
            
            return StudyDiaryFindAllResponse.builder()
                    .id(id)
                    .name(user.getName())
                    .title(title)
                    .content(content)
                    .likeNum(likeCount)
                    .commentNum(commentCount.intValue())
                    .createdAt(createdAt)
                    .build();
        });

        return studyDiaryFindAllResponsePage;
    }

//    public Page<StudyDiaryFindAllResponse> getWeeklyPopularStudyDiaries(Pageable pageable) {
//        // 이번 주 월요일~일요일 범위 계산
//        LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
//        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
//
//        List<StudyDiary> studyDiaries = studyDiaryRepository.findWeeklyPopularStudyDiaries(startOfWeek, endOfWeek);
//
//        //response로 전환
//        Page<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = studyDiaries.map(studyDiary -> {
//            return StudyDiaryFindAllResponse.builder()
//                    .id(studyDiary.getId())
//                    .name(studyDiary.getUser().getName())
//                    .title(studyDiary.getTitle())
//                    .content(studyDiary.getContent())
//                    .likeNum(studyDiary.getLikeCount())
//                    .commentNum(studyDiary.getComments().size())
//                    .createdAt(studyDiary.getCreatedAt())
//                    .build();
//        });
//
//        return studyDiaryFindAllResponsePage;
//    }

    public List<StudyDiaryFindAllResponse> getStudyDiaryDafts(Pageable pageable, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);
        List<StudyDiary> studyDiaryDraftList = studyDiaryRepository.findByIsCreatedFalse(user.getId());

        //response로 전환
        List<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = studyDiaryDraftList.stream().map(studyDiary -> {    //Page map으로 조작할때에는 stream 없이
            return StudyDiaryFindAllResponse.builder()
                    .id(studyDiary.getId())
                    .name(studyDiary.getUser().getName())
                    .title(studyDiary.getTitle())
                    .content(studyDiary.getContent())
                    .likeNum(studyDiary.getLikeCount())
                    .commentNum(studyDiary.getComments().size())
                    .createdAt(studyDiary.getCreatedAt())
                    .build();
        }).toList();

        return studyDiaryFindAllResponsePage;
    }

    public Object searchStudyDiaries(String keyword, Pageable pageable) {
        // 최적화된 검색 쿼리 사용
        Page<StudyDiarySearchResponse> searchResults = studyDiaryRepository.searchOptimized(keyword, pageable);
        
        // StudyDiarySearchResponse를 StudyDiaryFindAllResponse로 변환
        Page<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = searchResults.map(searchResult -> {
            return StudyDiaryFindAllResponse.builder()
                    .id(searchResult.getId())
                    .name(searchResult.getName())
                    .title(searchResult.getTitle())
                    .content(searchResult.getContentPreview())  // 전체 내용이 아닌 200자까지만 반환
                    .likeNum(searchResult.getLikeNum())
                    .commentNum(searchResult.getCommentNum().intValue())
                    // Long을 int로 변환, 기존에는 Comment List를 돌아보며, Like Query를 발생하는 것을 COUNT로 줄임
                    .createdAt(searchResult.getCreatedAt())
                    .build();
        });

        return studyDiaryFindAllResponsePage;
    }

    public StudyDiaryDetailResponse getStudyDiary(Long id) {
        StudyDiary studyDiary = studyDiaryRepository.findByIdWithUser(id)
                .orElseThrow(StudyDiaryNotFoundException::new);

        StudyDiaryDetailResponse studyDiaryDetailResponse = StudyDiaryDetailResponse.builder()
                .id(studyDiary.getId())
                .userId(studyDiary.getUser().getId())
                .name(studyDiary.getUser().getName())
                .title(studyDiary.getTitle())
                .content(studyDiary.getContent())
                .likeNum(studyDiary.getLikeCount())
                .createdAt(studyDiary.getCreatedAt())
                .commentList(studyDiary.getComments().stream()
                        .map(CommentDetailResponse::buildToResponse)
                        .toList())
                .build();

        return studyDiaryDetailResponse;
    }

    public List<StudyDiaryFindAllResponse> getUserStudyDiaries(Long userId, Pageable pageable) {
        User findUser = userRepository.findById(userId).orElseThrow(UserNotFoundForStudyDiaryException::new);
        List<Object[]> byUser = studyDiaryRepository.findByUser(findUser.getId());

        // Object[] 배열의 각 인덱스별 타입 매핑:
        // [0]: id (Long), [1]: user (User), [2]: title (String), [3]: content (String),
        // [4]: likeCount (Integer), [5]: commentCount (Long), [6]: createdAt (LocalDateTime)
        List<StudyDiaryFindAllResponse> studyDiaryFindAllResponses = byUser.stream().map(result -> {
            Long id = (Long) result[0];
            User user = (User) result[1];
            String title = (String) result[2];
            String content = (String) result[3];
            Integer likeCount = (Integer) result[4];
            Long commentCount = (Long) result[5];
            LocalDateTime createdAt = (LocalDateTime) result[6];
            
            return StudyDiaryFindAllResponse.builder()
                    .id(id)
                    .name(user.getName())
                    .title(title)
                    .content(content)
                    .likeNum(likeCount)
                    .commentNum(commentCount.intValue())
                    .createdAt(createdAt)
                    .build();
        }).toList();

        return studyDiaryFindAllResponses;
    }

    public List<StudyDiaryFindAllResponse> getMyStudyDiaries(UserDetails userDetails, Pageable pageable) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);
        List<Object[]> byUser = studyDiaryRepository.findByUser(findUser.getId());

        // Object[] 배열의 각 인덱스별 타입 매핑:
        // [0]: id (Long), [1]: user (User), [2]: title (String), [3]: content (String),
        // [4]: likeCount (Integer), [5]: commentCount (Long), [6]: createdAt (LocalDateTime)
        List<StudyDiaryFindAllResponse> studyDiaryFindAllResponses = byUser.stream().map(result -> {
            Long id = (Long) result[0];
            User user = (User) result[1];
            String title = (String) result[2];
            String content = (String) result[3];
            Integer likeCount = (Integer) result[4];
            Long commentCount = (Long) result[5];
            LocalDateTime createdAt = (LocalDateTime) result[6];
            
            return StudyDiaryFindAllResponse.builder()
                    .id(id)
                    .name(user.getName())
                    .title(title)
                    .content(content)
                    .likeNum(likeCount)
                    .commentNum(commentCount.intValue())
                    .createdAt(createdAt)
                    .build();
        }).toList();

        return studyDiaryFindAllResponses;
    }

    public Page<StudyDiaryMyHomeResponse> getMyRecentStudyDiaries(UserDetails userDetails, Pageable pageable) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        // 30일 전 날짜 계산
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        Page<StudyDiary> studyDiaries = studyDiaryRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(
                user.getId(), thirtyDaysAgo, pageable);

        // 현재 날짜와 시간
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        // Response DTO로 변환
        Page<StudyDiaryMyHomeResponse> responsePages = studyDiaries.map(studyDiary -> {
            // 오늘로부터 몇일 전인지 계산 (LocalDateTime 사용)
            LocalDate createdDate = studyDiary.getCreatedAt().toLocalDate();
            long daysAgo = calculateDaysAgo(createdDate, today);
            
            return StudyDiaryMyHomeResponse.builder()
                    .id(studyDiary.getId())
                    .title(studyDiary.getTitle())
                    .createdAt(studyDiary.getCreatedAt())
                    .daysAgo(daysAgo)
                    .build();
        });

        return responsePages;
    }

    //추후 구현(elastic search 필요)

//    public List<StudyDiary> getSimilarStudyDiaries(Long id) {
//    }
    @Transactional
    public Long saveDraft(StudyDiaryCreateRequest request, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary createdStudyDiary = StudyDiary.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isCreated(false)
                .user(user)
                .build();

        StudyDiary saved = studyDiaryRepository.save(createdStudyDiary);

        return saved.getId();
    }

    public StudyDiaryWeekStatusResponse getWeekStatus(Long userId) {
        //이번주 날짜 구하기
        LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        //초기 response 객체 생성
        StudyDiaryWeekStatusResponse response = StudyDiaryWeekStatusResponse.builder()
                .sunday(false)
                .monday(false)
                .tuesday(false)
                .wednesday(false)
                .thursday(false)
                .friday(false)
                .saturday(false)
                .todayStudyDiaryNum(0)
                .totalLike(0)
                .build();

        List<StudyDiary> weekStudyDiaries = studyDiaryRepository.findByUserIdAndCreatedAtBetween(userId, startOfWeek, endOfWeek);

        //주간 작성상황 작성
        weekStudyDiaries.stream().forEach(studyDiary -> {
            switch (studyDiary.getCreatedAt().getDayOfWeek()){
                case SUNDAY -> response.setSunday(true);
                case MONDAY -> response.setMonday(true);
                case TUESDAY -> response.setTuesday(true);
                case WEDNESDAY -> response.setWednesday(true);
                case THURSDAY -> response.setThursday(true);
                case FRIDAY -> response.setFriday(true);
                case SATURDAY -> response.setSaturday(true);
            }
        });

        //오늘 쓴 일기 갯수
        weekStudyDiaries.stream().forEach(studyDiary -> {
            if(studyDiary.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                response.setTodayStudyDiaryNum(response.getTodayStudyDiaryNum() + 1);
        });

        //stream 내부에서는 외부 변수 수정 불가, 아래와 같은 방법이 정석이라고 함
        int likeCount = weekStudyDiaries.stream()
                .mapToInt(StudyDiary::getLikeCount)
                .sum();
        response.setTotalLike(likeCount);

        return response;
    }

    public StudyDiaryWeekStatusResponse getMyWeekStatus(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);
        //이번주 날짜 구하기
        LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        //초기 response 객체 생성
        StudyDiaryWeekStatusResponse response = StudyDiaryWeekStatusResponse.builder()
                .sunday(false)
                .monday(false)
                .tuesday(false)
                .wednesday(false)
                .thursday(false)
                .friday(false)
                .saturday(false)
                .todayStudyDiaryNum(0)
                .totalLike(0)
                .build();

        log.info("start {} end {}", startOfWeek, endOfWeek);
        List<StudyDiary> weekStudyDiaries = studyDiaryRepository.findByUserIdAndCreatedAtBetween(user.getId(), startOfWeek, endOfWeek);
        log.info("week diaries {}", weekStudyDiaries.size());

        //주간 작성상황 작성
        weekStudyDiaries.stream().forEach(studyDiary -> {
            switch (studyDiary.getCreatedAt().getDayOfWeek()){
                case SUNDAY -> response.setSunday(true);
                case MONDAY -> response.setMonday(true);
                case TUESDAY -> response.setTuesday(true);
                case WEDNESDAY -> response.setWednesday(true);
                case THURSDAY -> response.setThursday(true);
                case FRIDAY -> response.setFriday(true);
                case SATURDAY -> response.setSaturday(true);
            }
        });

        //오늘 쓴 일기 갯수
        weekStudyDiaries.stream().forEach(studyDiary -> {
            if(studyDiary.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                response.setTodayStudyDiaryNum(response.getTodayStudyDiaryNum() + 1);
        });

        //총 Like 갯수 계산 - 전체 기간의 사용자 일기 조회
        LocalDateTime tenYearsAgo = LocalDateTime.now().minusYears(10);
        LocalDateTime now = LocalDateTime.now();
        List<StudyDiary> byUserAllStudyDiaryList = studyDiaryRepository.findByUserIdAndCreatedAtBetween(user.getId(), tenYearsAgo, now);
        //stream 내부에서는 외부 변수 수정 불가, 아래와 같은 방법이 정석이라고 함
        int likeCount = byUserAllStudyDiaryList.stream()
                .mapToInt(StudyDiary::getLikeCount)
                .sum();
        response.setTotalLike(likeCount);

        return response;
    }
    //추후구현
//    public Object exportStudyDiaries(Long userId) {

//    }

    @Transactional
    public Long createComment(Long studyDiaryId, CommentCreateRequest request, UserDetails userDetails) {
        StudyDiary studyDiary = studyDiaryRepository.findById(studyDiaryId).orElseThrow(StudyDiaryNotFoundException::new);
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);
        StudyDiaryComment comment = StudyDiaryComment.builder()
                .studyDiary(studyDiary)
                .user(user)
                .content(request.getContent())
                .build();
        studyDiaryCommentRepository.save(comment);

        // 알림 전송: 댓글 작성자가 글 작성자와 다를 경우에만 알림 전송
        if(!studyDiary.getUser().getId().equals(user.getId())) {
            notificationService.sendDiaryCommentNotification(
                    studyDiary.getUser(),// 글 작성자
                    studyDiary.getTitle(), // 글 제목
                    comment.getId(), // 댓글 ID
                    studyDiary.getId() // 배움일기 ID
            );
        }

        return comment.getId();
    }

    @Transactional
    public void deleteComment(Long studyDiaryId, Long commentId, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        // 배움일기 존재 여부 확인
        StudyDiary studyDiary = studyDiaryRepository.findById(studyDiaryId)
                .orElseThrow(StudyDiaryNotFoundException::new);

        // 댓글 존재 여부 확인
        StudyDiaryComment comment = studyDiaryCommentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        try {
            if(!user.getId().equals(comment.getUser().getId())){
                throw new UnauthorizedAccessException();
            }
        } catch (UnauthorizedAccessException e) {
            checkAdmin(user);
        }

        // 알림 제거 추가
        notificationService.deleteDiaryCommentNotification(studyDiary.getUser(), commentId);

        studyDiaryCommentRepository.delete(comment);
    }

    @Transactional
    public int toggleLike(Long studyDiaryId, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary studyDiary = studyDiaryRepository.findById(studyDiaryId)
                .orElseThrow(StudyDiaryNotFoundException::new);

        // 이미 좋아요를 눌렀는지 확인
        Optional<StudyDiaryLike> existingLike = studyDiary.getLikes()
                .stream().filter(like -> like.getUser().getId().equals(user.getId()))
                .findFirst();
//        log.info("Log userDetails {}, Like present {}", userDetails.getUsername(), existingLike.isPresent());
//        log.info("Log user {}, Like present {}", user.getId(), existingLike.isPresent());

        if (existingLike.isPresent()) {
//            log.info("Like {}", existingLike.get().getId());

            // 알림 제거 추가
            if(!studyDiary.getUser().getId().equals(user.getId())){
                notificationService.deleteDiaryLikeNotification(studyDiary.getUser(), studyDiary.getId());
            }

            // 좋아요 취소
            return studyDiary.deleteLike(user.getId());
        } else {
            // 좋아요 추가
            int likeCount = studyDiary.addLike(user);

            // 알림 전송: 좋아요를 누른 사용자가 글 작성자와 다를 경우에만 알림 전송
            if (!studyDiary.getUser().getId().equals(user.getId())){
                notificationService.sendDiaryLikeNotification(
                        studyDiary.getUser(), // 글 작성자
                        studyDiary.getTitle(), // 글 제목
                        studyDiary.getId() // 배움일기 ID
                );
            }
            return likeCount;
        }
        //return 값으로 최신 좋아요 갯수
    }

    private void checkUser(User user, StudyDiary studyDiary) {
        if(!user.getId().equals(studyDiary.getUser().getId())){
            throw new UnauthorizedAccessException();
        }
    }

    private void checkAdmin(User user) {
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new UnauthorizedAccessException();
        }
    }

    private long calculateDaysAgo(LocalDate createdDate, LocalDate today) {
        // 년, 월, 일을 각각 비교하여 날짜 차이 계산
        int yearDiff = today.getYear() - createdDate.getYear();
        int monthDiff = today.getMonthValue() - createdDate.getMonthValue();
        int dayDiff = today.getDayOfMonth() - createdDate.getDayOfMonth();
        
        // 전체 일수로 변환
        if (yearDiff == 0 && monthDiff == 0) {
            // 같은 년, 월인 경우
            return Math.abs(dayDiff);
        } else {
            // 다른 년도 또는 월인 경우 - 더 정확한 계산
            long totalDays = 0;
            LocalDate start = createdDate;
            LocalDate end = today;
            
            // 시작 날짜가 더 늦은 경우 순서 바꾸기
            if (start.isAfter(end)) {
                start = today;
                end = createdDate;
            }
            
            // 날짜별로 하나씩 증가시키면서 계산
            while (!start.equals(end)) {
                start = start.plusDays(1);
                totalDays++;
            }
            
            return totalDays;
        }
    }
}
