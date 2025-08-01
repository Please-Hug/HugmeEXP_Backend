package org.example.hugmeexp.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.recruitment.entity.RecruitmentBookmark;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.exception.DuplicateRecruitmentBookmarkException;
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

        Recruitment recruitment = recruitmentService.getRecruitmentById(recruitmentId);

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());

        if (recruitmentBookmarkRepository.existsByUserAndRecruitment(user, recruitment)) {
            throw new DuplicateRecruitmentBookmarkException();
        }

        RecruitmentBookmark recruitmentBookmark = RecruitmentBookmark.builder()
                .user(user)
                .recruitment(recruitment)
                .build();

        recruitmentBookmarkRepository.save(recruitmentBookmark);
    }
}
