package org.example.hugmeexp.domain.studyRoom.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.entity.Location;
import org.example.hugmeexp.domain.studyRoom.util.DistanceCalculator;
import org.example.hugmeexp.global.common.config.KakaoMapConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMapService {

    private final KakaoMapConfig kakaoMapConfig;
    private final RestTemplate restTemplate;
    @Qualifier("kakaoMapObjectMapper")
    private final ObjectMapper objectMapper;

    /**
     * 주소를 좌표로 변환 (Geocoding)
     */
    public Optional<Location> addressToCoordinates(String address) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(kakaoMapConfig.getBaseUrl() + kakaoMapConfig.getGeocodingUrl())
                    .queryParam("query", address)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoMapConfig.getApiKey());

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");

            if (documents.isArray() && documents.size() > 0) {
                JsonNode firstResult = documents.get(0);
                Double longitude = firstResult.path("x").asDouble();
                Double latitude = firstResult.path("y").asDouble();
                String resultAddress = firstResult.path("address_name").asText();

                log.info("Geocoding success - address: {} -> lat: {}, lng: {}", address, latitude, longitude);
                return Optional.of(Location.of(latitude, longitude, resultAddress));
            }

            log.warn("Geocoding failed - no results for address: {}", address);
            return Optional.empty();

        } catch (Exception e) {
            log.error("Geocoding error for address: {}", address, e);
            return Optional.empty();
        }
    }

    /**
     * 좌표를 주소로 변환 (Reverse Geocoding)
     */
    public Optional<String> coordinatesToAddress(Double latitude, Double longitude) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(kakaoMapConfig.getBaseUrl() + kakaoMapConfig.getCoordToAddressUrl())
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoMapConfig.getApiKey());

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");

            if (documents.isArray() && documents.size() > 0) {
                JsonNode firstResult = documents.get(0);
                String address = firstResult.path("address").path("address_name").asText();

                log.info("Reverse geocoding success - lat: {}, lng: {} -> address: {}", latitude, longitude, address);
                return Optional.of(address);
            }

            log.warn("Reverse geocoding failed - no results for coordinates: {}, {}", latitude, longitude);
            return Optional.empty();

        } catch (Exception e) {
            log.error("Reverse geocoding error for coordinates: {}, {}", latitude, longitude, e);
            return Optional.empty();
        }
    }


    /**
     * 두 지점 간의 거리 계산
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 거리 (km)
     */
    public Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        return DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
    }

//    /** 카카오맵 API를 사용한 실제 경로 거리 계산 (향후 확장 가능)
//     * 카카오맵 길찾기 API 연동하여 실제 이동 거리 계산 가능
//     * @param lat1
//     * @param lon1
//     * @param lat2
//     * @param lon2
//     * @return
//     */
//    public Double calculateRoutingDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
//        return DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
//    }
}