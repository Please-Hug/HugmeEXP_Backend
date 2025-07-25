@host = localhost
@port = 8080
@baseUrl = http://{{host}}:{{port}}/api/v1/studyroom

### 1. 모든 스터디홀 위치 조회 (지도용)
GET {{baseUrl}}/map/halls
Content-Type: application/json

### 2. 현재 위치 기반 주변 스터디홀 검색 (강남역 기준)
POST {{baseUrl}}/map/nearby
Content-Type: application/json

{
    "latitude": 37.4979,
        "longitude": 127.0276,
        "radius": 5.0,
        "limit": 10
}

### 3. 현재 위치 기반 주변 스터디홀 검색 (홍대입구역 기준)
POST {{baseUrl}}/map/nearby
Content-Type: application/json

{
    "latitude": 37.5563,
        "longitude": 126.9245,
        "radius": 3.0,
        "limit": 5
}

### 4. 특정 스터디홀 상세 정보 조회
GET {{baseUrl}}/halls/1
Content-Type: application/json

### 5. 현재 위치로부터 특정 스터디홀까지의 거리 계산
GET {{baseUrl}}/halls/1/distance?latitude=37.5563&longitude=126.9245
Content-Type: application/json

### 6. 주소로 스터디홀 검색
GET {{baseUrl}}/search/address?address=강남
Content-Type: application/json

### 7. 이름으로 스터디홀 검색
GET {{baseUrl}}/search/name?name=스터디카페
Content-Type: application/json

### 8. 넓은 반경으로 주변 스터디홀 검색 (서울 중심가 기준)
POST {{baseUrl}}/map/nearby
Content-Type: application/json

{
    "latitude": 37.5665,
        "longitude": 126.9780,
        "radius": 15.0,
        "limit": 20
}

### 9. 좁은 반경으로 주변 스터디홀 검색 (정확한 위치 기준)
POST {{baseUrl}}/map/nearby
Content-Type: application/json

{
    "latitude": 37.5007,
        "longitude": 127.0366,
        "radius": 1.0,
        "limit": 3
}

### 10. 다른 지역 스터디홀 검색 (잠실 기준)
POST {{baseUrl}}/map/nearby
Content-Type: application/json

{
    "latitude": 37.5133,
        "longitude": 127.1003,
        "radius": 2.0,
        "limit": 5
}