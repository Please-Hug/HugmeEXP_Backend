package org.example.hugmeexp.domain.recruitment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.recruitment.dto.QRecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.dto.RecruitmentSearchConditionDTO;
import org.example.hugmeexp.domain.recruitment.entity.QCompany;
import org.example.hugmeexp.domain.recruitment.entity.QRecruitment;
import org.example.hugmeexp.domain.recruitment.entity.QTag;
import org.example.hugmeexp.domain.recruitment.entity.QTechStack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class RecruitmentRepositoryImpl implements RecruitmentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RecruitmentResponseDTO> findBySearchConditions(RecruitmentSearchConditionDTO cond, Pageable pageable) {
        QRecruitment r = QRecruitment.recruitment;
        QCompany c = QCompany.company;
        QTechStack ts = QTechStack.techStack;
        QTag t = QTag.tag;

        // Group By 후 having 절에서 사용할 count 컬럼
        NumberExpression<Long> techStackCount = ts.id.count();
        NumberExpression<Long> tagCount = t.id.count();

        List<RecruitmentResponseDTO> content = queryFactory
                .select(new QRecruitmentResponseDTO(
                        r.id, r.recruitmentSourceId, r.title, c.companyName, c.companyImageUrl, r.dueDate,
                        r.experienceMin, r.experienceMax, r.workLocation, r.latitude, r.longitude, r.modifiedAt
                ))
                .from(r)
                .join(r.company, c)
                .leftJoin(r.techStacks, ts)
                .leftJoin(r.tags, t)
                .where(
                        r.dueDate.gt(LocalDateTime.now()),
                        salaryMinGoe(cond.getSalaryMin()),
                        salaryMaxLoe(cond.getSalaryMax()),
                        experienceBetween(cond.getExperienceMin(), cond.getExperienceMax()),
                        educationEq(cond.getEducation()),
                        workLocationLike(cond.getWorkLocation()),
                        withinBounds(cond),
                        keywordLike(cond.getKeyword()),
                        techStacksIn(cond.getTechStacks()),
                        tagsIn(cond.getTags())
                )
                .groupBy(r.id)
                .having(
                        techStackCountEq(techStackCount, cond.getTechStacks(), cond.getTechStackCount()),
                        tagCountEq(tagCount, cond.getTags(), cond.getTagCount())
                )
                .orderBy(r.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(r.id.countDistinct())
                .from(r)
                .join(r.company, c)
                .leftJoin(r.techStacks, ts)
                .leftJoin(r.tags, t)
                .where(
                        r.dueDate.gt(LocalDateTime.now()),
                        salaryMinGoe(cond.getSalaryMin()),
                        salaryMaxLoe(cond.getSalaryMax()),
                        experienceBetween(cond.getExperienceMin(), cond.getExperienceMax()),
                        educationEq(cond.getEducation()),
                        workLocationLike(cond.getWorkLocation()),
                        withinBounds(cond),
                        keywordLike(cond.getKeyword()),
                        techStacksIn(cond.getTechStacks()),
                        tagsIn(cond.getTags())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // 조건 메서드들

    private BooleanExpression salaryMinGoe(Integer min) {
        return min != null ? QRecruitment.recruitment.salaryMin.goe(min) : null;
    }

    private BooleanExpression salaryMaxLoe(Integer max) {
        return max != null ? QRecruitment.recruitment.salaryMax.loe(max) : null;
    }

    private BooleanExpression experienceBetween(Integer min, Integer max) {
        if (min == null || max == null) return null;
        return QRecruitment.recruitment.experienceMax.goe(min)
                .and(QRecruitment.recruitment.experienceMin.loe(max));
    }

    private BooleanExpression educationEq(Integer education) {
        return education != null ? QRecruitment.recruitment.education.eq(education) : null;
    }

    private BooleanExpression workLocationLike(String location) {
        return location != null ? QRecruitment.recruitment.workLocation.contains(location) : null;
    }

    private BooleanExpression withinBounds(RecruitmentSearchConditionDTO cond) {
        if (cond.getTopLeftLat() == null || cond.getTopLeftLng() == null ||
                cond.getBottomRightLat() == null || cond.getBottomRightLng() == null)
            return null;

        BigDecimal minLat = cond.getTopLeftLat().min(cond.getBottomRightLat());
        BigDecimal maxLat = cond.getTopLeftLat().max(cond.getBottomRightLat());
        BigDecimal minLng = cond.getTopLeftLng().min(cond.getBottomRightLng());
        BigDecimal maxLng = cond.getTopLeftLng().max(cond.getBottomRightLng());

        return QRecruitment.recruitment.latitude.between(minLat, maxLat)
                .and(QRecruitment.recruitment.longitude.between(minLng, maxLng));
    }

    private BooleanExpression keywordLike(String keyword) {
        if (keyword == null) return null;
        return QRecruitment.recruitment.title.containsIgnoreCase(keyword)
                .or(QCompany.company.companyName.containsIgnoreCase(keyword));
    }

    private BooleanExpression techStacksIn(List<Long> techStacks) {
        return techStacks != null && !techStacks.isEmpty() ? QTechStack.techStack.techItem.id.in(techStacks) : null;
    }

    private BooleanExpression tagsIn(List<Long> tags) {
        return tags != null && !tags.isEmpty() ? QTag.tag.tagItem.id.in(tags) : null;
    }

    private BooleanExpression techStackCountEq(NumberExpression<Long> countExpr, List<Long> techStacks, Long count) {
        return techStacks != null && count != null ? countExpr.eq(count) : null;
    }

    private BooleanExpression tagCountEq(NumberExpression<Long> countExpr, List<Long> tags, Long count) {
        return tags != null && count != null ? countExpr.eq(count) : null;
    }
}
