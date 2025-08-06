package org.example.hugmeexp.domain.studyRoom.reids.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis Trie 자동완성 서비스
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisAutoCompleteService {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String AUTOCOMPLETE_PREFIX = "autocomplete:";
    private static final String POPULAR_KEYWORDS_KEY = "popular_keywords";

    /**
     * 검색어 인덱싱 (Trie 구조)
     */
    public void indexSearchTerms(StudyHall studyHall) {
        try {
            List<String> terms = extractSearchTerms(studyHall);

            for (String term : terms) {
                String normalizedTerm = normalizeKeyword(term);

                // 각 글자별로 prefix 생성하여 저장
                for (int i = 1; i <= normalizedTerm.length(); i++) {
                    String prefix = normalizedTerm.substring(0, i);
                    String key = AUTOCOMPLETE_PREFIX + prefix;

                    // Set에 완전한 검색어 저장 (중복 제거)
                    stringRedisTemplate.opsForSet().add(key, term);

                    // TTL 설정 (1일)
                    stringRedisTemplate.expire(key, Duration.ofDays(1));
                }
            }

            log.debug("스터디홀 {} 검색어 인덱싱 완료: {}", studyHall.getId(), terms);

        } catch (Exception e) {
            log.error("스터디홀 {} 검색어 인덱싱 실패", studyHall.getId(), e);
        }
    }

    /**
     * 자동완성 제안 조회
     */
    public List<String> getAutocompleteSuggestions(String prefix, int limit) {
        if (prefix == null || prefix.length() < 2) {
            return getPopularKeywords(limit);
        }

        try {
            String normalizedPrefix = normalizeKeyword(prefix);
            String key = AUTOCOMPLETE_PREFIX + normalizedPrefix;

            Set<String> suggestions = stringRedisTemplate.opsForSet().members(key);

            if (suggestions == null || suggestions.isEmpty()) {
                return getPopularKeywords(limit);
            }

            // 인기도 순으로 정렬하여 반환
            return suggestions.stream()
                    .sorted(this::compareByPopularity)
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("자동완성 조회 실패 - prefix: {}", prefix, e);
            return Collections.emptyList();
        }
    }

    /**
     * 검색어 인기도 기록
     */
    public void recordSearch(String keyword) {
        try {
            if (StringUtils.hasText(keyword)) {
                String normalized = normalizeKeyword(keyword);
                stringRedisTemplate.opsForZSet().incrementScore(POPULAR_KEYWORDS_KEY, normalized, 1);

                // 인기 검색어는 7일간 유지
                stringRedisTemplate.expire(POPULAR_KEYWORDS_KEY, Duration.ofDays(7));
            }
        } catch (Exception e) {
            log.error("검색어 기록 실패 - keyword: {}", keyword, e);
        }
    }

    /**
     * 인기 검색어 조회
     */
    public List<String> getPopularKeywords(int limit) {
        try {
            Set<String> popular = stringRedisTemplate.opsForZSet()
                    .reverseRange(POPULAR_KEYWORDS_KEY, 0, limit - 1);

            return popular != null ? new ArrayList<>(popular) : Collections.emptyList();
        } catch (Exception e) {
            log.error("인기 검색어 조회 실패", e);
            return Collections.emptyList();
        }
    }

    // === Private Helper Methods ===

    private List<String> extractSearchTerms(StudyHall studyHall) {
        List<String> terms = new ArrayList<>();

        if (StringUtils.hasText(studyHall.getName())) {
            terms.add(studyHall.getName());
            // 이름의 개별 단어들도 추가
            terms.addAll(Arrays.asList(studyHall.getName().split("\\s+")));
        }

        if (StringUtils.hasText(studyHall.getSimpleAddress())) {
            terms.add(studyHall.getSimpleAddress());
            terms.addAll(Arrays.asList(studyHall.getSimpleAddress().split("\\s+")));
        }

        return terms.stream()
                .filter(term -> term.length() >= 2) // 2글자 이상만
                .distinct()
                .collect(Collectors.toList());
    }

    private String normalizeKeyword(String keyword) {
        return keyword.toLowerCase()
                .trim()
                .replaceAll("[^가-힣a-z0-9\\s]", "") // 특수문자 제거
                .replaceAll("\\s+", " "); // 공백 정규화
    }

    private int compareByPopularity(String a, String b) {
        // ZSet에서 점수 조회하여 인기도 비교
        Double scoreA = stringRedisTemplate.opsForZSet().score(POPULAR_KEYWORDS_KEY, normalizeKeyword(a));
        Double scoreB = stringRedisTemplate.opsForZSet().score(POPULAR_KEYWORDS_KEY, normalizeKeyword(b));

        return Double.compare(
                scoreB != null ? scoreB : 0.0,
                scoreA != null ? scoreA : 0.0
        );
    }

    /**
     * 전체 자동완성 데이터 재구축
     */
    public void rebuildAutocompleteIndex(List<StudyHall> studyHalls) {
        try {
            // 기존 자동완성 데이터 삭제
            Set<String> keys = stringRedisTemplate.keys(AUTOCOMPLETE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);
            }

            // 새 데이터 인덱싱
            for (StudyHall hall : studyHalls) {
                indexSearchTerms(hall);
            }

            log.info("자동완성 인덱스 재구축 완료 - {} 개 스터디홀", studyHalls.size());

        } catch (Exception e) {
            log.error("자동완성 인덱스 재구축 실패", e);
        }
    }
}
