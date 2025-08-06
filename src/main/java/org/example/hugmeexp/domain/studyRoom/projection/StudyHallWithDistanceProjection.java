package org.example.hugmeexp.domain.studyRoom.projection;

import java.time.LocalTime;

public interface StudyHallWithDistanceProjection {
    Long getId();
    String getName();
    String getDescription();
    Double getLatitude();
    Double getLongitude();
    String getAddress();
    String getSimpleAddress();
    String getThumbnail();
    LocalTime getOpenTime();
    LocalTime getCloseTime();
    Double getDistance();
}

