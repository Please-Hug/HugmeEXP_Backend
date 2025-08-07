package org.example.hugmeexp.domain.studyRoom.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.repository.StudyHallRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Redis 데이터 동기화 서비스
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisDataSyncService {

    private final RedisGeoService redisGeoService;
    private final RedisAutoCompleteService autoCompleteService;
    private final StudyHallRepository studyHallRepository;

    /**
     * 애플리케이션 시작시 전체 데이터 동기화 (조용히 처리)
     */
    @EventListener(ApplicationReadyEvent.class)
    public void syncAllDataOnStartup() {
        try {
            List<StudyHall> allHalls = studyHallRepository.findAllWithStudyRooms();

            // Redis Geo 데이터 동기화
            redisGeoService.reindexAllStudyHalls(allHalls);

            // 자동완성 데이터 동기화
            autoCompleteService.rebuildAutocompleteIndex(allHalls);

            log.info("Redis 초기 동기화 완료 - {} 개 스터디홀", allHalls.size());

        } catch (Exception e) {
            log.warn("Redis 초기 동기화 실패 (DB 검색으로 fallback 작동)", e);
        }
    }
}