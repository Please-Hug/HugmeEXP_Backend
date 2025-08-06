package org.example.hugmeexp.domain.studyRoom.reids.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.entity.StudyHall;
import org.example.hugmeexp.domain.studyRoom.reids.event.StudyHallCreatedEvent;
import org.example.hugmeexp.domain.studyRoom.reids.event.StudyHallDeletedEvent;
import org.example.hugmeexp.domain.studyRoom.reids.event.StudyHallUpdatedEvent;
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

    /**s
     * 애플리케이션 시작시 전체 데이터 동기화
     */
    @EventListener(ApplicationReadyEvent.class)
    public void syncAllDataOnStartup() {
        log.info("Redis 데이터 동기화 시작...");

        try {
            List<StudyHall> allHalls = studyHallRepository.findAllWithStudyRooms();

            // Redis Geo 데이터 동기화
            redisGeoService.reindexAllStudyHalls(allHalls);

            // 자동완성 데이터 동기화
            autoCompleteService.rebuildAutocompleteIndex(allHalls);

            log.info("Redis 데이터 동기화 완료 - {} 개 스터디홀", allHalls.size());

        } catch (Exception e) {
            log.error("Redis 데이터 동기화 실패", e);
        }
    }

    /**
     * 스터디홀 생성시 Redis 동기화
     */
    @EventListener
    public void onStudyHallCreated(StudyHallCreatedEvent event) {
        StudyHall studyHall = event.getStudyHall();
        redisGeoService.indexStudyHallLocation(studyHall);
        autoCompleteService.indexSearchTerms(studyHall);
    }

    /**
     * 스터디홀 수정시 Redis 동기화
     */
    @EventListener
    public void onStudyHallUpdated(StudyHallUpdatedEvent event) {
        StudyHall studyHall = event.getStudyHall();
        redisGeoService.indexStudyHallLocation(studyHall); // 덮어쓰기
        autoCompleteService.indexSearchTerms(studyHall);
    }

    /**
     * 스터디홀 삭제시 Redis 정리
     */
    @EventListener
    public void onStudyHallDeleted(StudyHallDeletedEvent event) {
        redisGeoService.removeStudyHallLocation(event.getStudyHallId());
    }
}
