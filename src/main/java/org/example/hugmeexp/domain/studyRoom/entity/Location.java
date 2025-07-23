package org.example.hugmeexp.domain.studyRoom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private Double latitude;   // 위도
    private Double longitude;  // 경도
    private String address;    // 주소

    public static Location of(Double latitude, Double longitude) {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public static Location of(Double latitude, Double longitude, String address) {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .build();
    }
}