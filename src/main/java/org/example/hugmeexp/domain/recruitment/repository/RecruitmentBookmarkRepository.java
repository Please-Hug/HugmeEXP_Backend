package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.entity.RecruitmentBookmark;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitmentBookmarkRepository extends JpaRepository<RecruitmentBookmark, Long> {

    /**
     * 주어진 사용자와 채용 공고 조합의 즐겨찾기 여부를 확인합니다.
     *
     * @param user 즐겨찾기 등록 여부를 확인할 사용자
     * @param recruitment 즐겨찾기 등록 여부를 확인할 채용 공고
     * @return 해당 사용자가 해당 채용 공고를 즐겨찾기 했는지 여부
     */
    boolean existsByUserAndRecruitment(User user, Recruitment recruitment);

    /**
     * 사용자와 채용 공고를 기준으로 즐겨찾기 엔티티를 조회합니다.
     *
     * @param user 즐겨찾기 정보를 조회할 사용자
     * @param recruitment 즐겨찾기 정보를 조회할 채용 공고
     * @return 사용자가 해당 채용 공고를 즐겨찾기한 경우 Optional<RecruitmentBookmark>를 반환하며,
     *         존재하지 않으면 Optional.empty()를 반환합니다.
     */
    Optional<RecruitmentBookmark> findByUserAndRecruitment(User user, Recruitment recruitment);

    /**
     * 특정 사용자가 등록한 모든 즐겨찾기 목록을 조회합니다.
     *
     * @param user 즐겨찾기 목록을 조회할 사용자
     * @return 해당 사용자가 등록한 즐겨찾기 목록 (RecruitmentBookmark 리스트)
     */
    List<RecruitmentBookmark> findAllByUser(User user);
}
