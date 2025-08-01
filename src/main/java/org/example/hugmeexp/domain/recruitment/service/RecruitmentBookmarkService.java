package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.entity.RecruitmentBookmark;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.exception.DuplicateRecruitmentBookmarkException;
import org.example.hugmeexp.domain.recruitment.exception.RecruitmentBookmarkNotFoundException;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentBookmarkRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.exception.UserNotFoundException;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentBookmarkService {

    private final RecruitmentBookmarkRepository recruitmentBookmarkRepository;
    private final UserRepository userRepository;
    private final RecruitmentService recruitmentService;

    /**
     * 즐겨찾기를 등록합니다.
     *
     * @param userId 즐겨찾기를 등록할 사용자 ID
     * @param recruitmentId 즐겨찾기 공고 ID
     */
    @Transactional
    public void addBookmark (Long userId, Long recruitmentId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());

        Recruitment recruitment = recruitmentService.getRecruitmentById(recruitmentId);

        if (recruitmentBookmarkRepository.existsByUserAndRecruitment(user, recruitment)) {
            throw new DuplicateRecruitmentBookmarkException();
        }

        RecruitmentBookmark recruitmentBookmark = RecruitmentBookmark.builder()
                .user(user)
                .recruitment(recruitment)
                .build();

        recruitmentBookmarkRepository.save(recruitmentBookmark);
    }

    /**
     * 즐겨찾기를 취소(삭제)합니다.
     * 사용자 ID와 채용 공고 ID를 기반으로 해당 즐겨찾기 엔티티를 조회하여 삭제합니다.
     *
     * @param userId 즐겨찾기를 취소할 사용자 ID
     * @param recruitmentId 즐겨찾기를 취소할 채용 공고 ID
     */
    @Transactional
    public void removeBookmark(Long userId, Long recruitmentId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());

        Recruitment recruitment = recruitmentService.getRecruitmentById(recruitmentId);

        RecruitmentBookmark recruitmentBookmark = recruitmentBookmarkRepository.findByUserAndRecruitment(user, recruitment)
                .orElseThrow(() -> new RecruitmentBookmarkNotFoundException());

        recruitmentBookmarkRepository.delete(recruitmentBookmark);

    }
}
