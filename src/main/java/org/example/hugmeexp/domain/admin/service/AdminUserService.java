package org.example.hugmeexp.domain.admin.service;

import org.example.hugmeexp.domain.admin.dto.request.RoleChangeRequest;
import org.example.hugmeexp.domain.admin.dto.response.AdminUserAllResponse;
import org.example.hugmeexp.domain.admin.dto.response.AdminUserInfoResponse;
import org.example.hugmeexp.domain.admin.dto.response.MonthlyRegistrationStatsResponse;
import org.example.hugmeexp.domain.admin.mapper.AdminUserResponseMapper;
import org.example.hugmeexp.domain.user.dto.request.UserUpdateRequest;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserService userService;

    /** 1) 회원 목록 페이징 조회 */
    @Transactional(readOnly = true)
    @Cacheable(value = "adminUserList", key = "#pageable.pageNumber + '::' + #pageable.pageSize + '::' + #pageable.sort.toString()")
    public Page<AdminUserAllResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> {
                    UserInfoResponse userInfo = userService.getUserInfoResponse(user);  // UserService 메서드 호출
                    return AdminUserResponseMapper.toProfileResponse(user, userInfo);
                });
    }

    /** 2) 단일 회원 상세 조회 */
    @Transactional(readOnly = true)
    @Cacheable(value = "adminUserInfo", key = "#username")
    public AdminUserInfoResponse getUserByAdmin(String username) {
        User u = userService.findByUsername(username);
        UserInfoResponse base = userService.getUserInfoResponse(u);
        return AdminUserResponseMapper.toInfoResponse(u, base);
    }

    /** 3) 회원 정보 수정 */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "adminUserInfo", key = "#username"),
            @CacheEvict(value = "adminUserList", allEntries = true),
            @CacheEvict(value = "userInfo", key = "#username") // UserService 캐시도 무효화
    })
    public AdminUserInfoResponse updateUserByAdmin(String username, UserUpdateRequest req) {
        User u = userService.findByUsername(username);
        UserInfoResponse updatedBase = userService.updateUserInfo(u, req);
        return AdminUserResponseMapper.toInfoResponse(u, updatedBase);
    }

    /** 4) 회원 삭제 */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "adminUserInfo", key = "#username"),
            @CacheEvict(value = "adminUserList", allEntries = true),
            @CacheEvict(value = "userInfo", key = "#username"),
    })
    public AdminUserInfoResponse deleteUserByAdmin(String username) {
        User u = userService.findByUsername(username);
        UserInfoResponse deletedBase = userService.getUserInfoResponse(u);
        AdminUserInfoResponse response = AdminUserResponseMapper.toInfoResponse(u, deletedBase);
        userService.deleteByUsername(username);
        return response;
    }

    /** 5) 권한 변경 */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "adminUserInfo", key = "#username"),
            @CacheEvict(value = "adminUserList", allEntries = true),
            @CacheEvict(value = "userInfo", key = "#username") // UserService 캐시도 무효화
    })
    public AdminUserInfoResponse changeUserRole(String username, RoleChangeRequest req) {
        User u = userService.findByUsername(username);
        u.changeRole(req.getRole());
        UserInfoResponse base = userService.getUserInfoResponse(u);
        return AdminUserResponseMapper.toInfoResponse(u, base);
    }

    /**
     * 최근 12개월 월별 가입자 통계 조회
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "monthlyRegistrationStats", key = "'all'", unless = "#result.isEmpty()")
    public List<MonthlyRegistrationStatsResponse> getMonthlyRegistrationStats() {
        List<MonthlyRegistrationStatsResponse> stats = new ArrayList<>();

        // 현재 월부터 12개월 전까지
        YearMonth currentMonth = YearMonth.now();

        Long previousCount = null; // 전월 데이터 (증감률 계산용)

        for (int i = 11; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);

            // 해당 월의 시작일과 다음 월 시작일
            LocalDateTime startOfMonth = targetMonth.atDay(1).atStartOfDay();
            LocalDateTime startOfNextMonth = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

            // 해당 월의 가입자 수 조회
            long count = userRepository.countByCreatedAtBetween(startOfMonth, startOfNextMonth);

            // 증감률 계산 (전월 대비)
            Double growthRate = null;
            if (previousCount != null && previousCount > 0) {
                growthRate = ((double) (count - previousCount) / previousCount) * 100;
                growthRate = Math.round(growthRate * 100.0) / 100.0; // 소수점 2자리까지
            }

            MonthlyRegistrationStatsResponse response = MonthlyRegistrationStatsResponse.builder()
                    .month(targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .count((int) count)
                    .growthRate(growthRate)
                    .build();

            stats.add(response);
            previousCount = count;
        }

        return stats;
    }

    /**
     * 특정 기간의 월별 가입자 통계 조회
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "monthlyRegistrationStats", key = "#months", unless = "#result.isEmpty()")
    public List<MonthlyRegistrationStatsResponse> getMonthlyRegistrationStats(int months) {
        if (months <= 0 || months > 24) {
            throw new IllegalArgumentException("조회 기간은 1~24개월 사이여야 합니다.");
        }
        // months 파라미터로 조회할 개월 수 지정 가능
        List<MonthlyRegistrationStatsResponse> stats = new ArrayList<>();

        YearMonth currentMonth = YearMonth.now();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);

            LocalDateTime startOfMonth = targetMonth.atDay(1).atStartOfDay();
            LocalDateTime startOfNextMonth = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

            // 해당 월의 가입자 수 조회
            long count = userRepository.countByCreatedAtBetween(startOfMonth, startOfNextMonth);

            MonthlyRegistrationStatsResponse response = MonthlyRegistrationStatsResponse.builder()
                    .month(targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .count((int) count)
                    .growthRate(null) // 단순 버전에서는 증감률 제외
                    .build();

            stats.add(response);
        }

        return stats;
    }

}