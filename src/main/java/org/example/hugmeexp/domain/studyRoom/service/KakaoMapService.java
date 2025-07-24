package org.example.hugmeexp.domain.studyRoom.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studyRoom.entity.Location;
import org.example.hugmeexp.global.common.config.KakaoMapConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    public Location addressToCoordinates(String address) {
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
                return Location.of(latitude, longitude, resultAddress);
            }

            log.warn("Geocoding failed - no results for address: {}", address);
            return null;

        } catch (Exception e) {
            log.error("Geocoding error for address: {}", address, e);
            return null;
        }
    }

    /**
     * 좌표를 주소로 변환 (Reverse Geocoding)
     */
    public String coordinatesToAddress(Double latitude, Double longitude) {
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
                return address;
            }

            log.warn("Reverse geocoding failed - no results for coordinates: {}, {}", latitude, longitude);
            return null;

        } catch (Exception e) {
            log.error("Reverse geocoding error for coordinates: {}, {}", latitude, longitude, e);
            return null;
        }
    }

    /**
     * 두 지점 간의 거리 계산 (Haversine formula)
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 거리 (km)
     */
    public Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return null;
        }

        final int EARTH_RADIUS = 6371; // 지구 반지름 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return Math.round(distance * 100.0) / 100.0; // 소수점 둘째 자리까지
    }
}