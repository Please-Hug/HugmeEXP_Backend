package org.example.hugmeexp.domain.recruitment.repository;

import org.example.hugmeexp.domain.recruitment.entity.RecruitmentBookmark;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
