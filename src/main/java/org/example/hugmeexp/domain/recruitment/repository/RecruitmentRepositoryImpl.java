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
                .where(buildWhereClause(r,cond))
                .groupBy(r.id)
                .having(buildHavingClause(techStackCount, cond.getTechStacks(), cond.getTechStackCount(),
                        tagCount, cond.getTags(), cond.getTagCount()))
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
                .where(buildWhereClause(r,cond))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression buildWhereClause(QRecruitment r,RecruitmentSearchConditionDTO cond) {
        return r.dueDate.gt(LocalDateTime.now())
                .and(salaryMinGoe(cond.getSalaryMin()))
                .and(salaryMaxLoe(cond.getSalaryMax()))
                .and(experienceBetween(cond.getExperienceMin(), cond.getExperienceMax()))
                .and(educationEq(cond.getEducation()))
                .and(workLocationLike(cond.getWorkLocation()))
                .and(withinBounds(cond))
                .and(keywordLike(cond.getKeyword()))
                .and(techStacksIn(cond.getTechStacks()))
                .and(tagsIn(cond.getTags()));
    }

    private BooleanExpression buildHavingClause(NumberExpression<Long> techStackCountExpr, List<Long> techStacks, Long techStackCount,
                                                NumberExpression<Long> tagCountExpr, List<Long> tags, Long tagCount) {
        BooleanExpression techExpr = techStacks != null && techStackCount != null ? techStackCountExpr.eq(techStackCount) : null;
        BooleanExpression tagExpr = tags != null && tagCount != null ? tagCountExpr.eq(tagCount) : null;
        return techExpr != null ? (tagExpr != null ? techExpr.and(tagExpr) : techExpr) : tagExpr;
    }

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
                cond.getBottomRightLat() == null || cond.getBottomRightLng() == null) return null;

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
}
