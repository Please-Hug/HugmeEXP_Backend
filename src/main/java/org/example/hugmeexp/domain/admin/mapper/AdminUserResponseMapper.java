package org.example.hugmeexp.domain.admin.mapper;

import org.example.hugmeexp.domain.admin.dto.response.AdminUserAllResponse;
import org.example.hugmeexp.domain.admin.dto.response.AdminUserInfoResponse;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.entity.User;

public class AdminUserResponseMapper {

    /** 목록 조회용 매핑 */
    public static AdminUserAllResponse toProfileResponse(User u, UserInfoResponse userInfo) {
        return new AdminUserAllResponse(
                u.getId(),
                u.getPublicProfileImageUrl() != null ? u.getPublicProfileImageUrl() : null,
                u.getUsername(),
                u.getName(),
                u.getRole(),
                userInfo.getLevel(),
                userInfo.getPoint()
        );
    }

    /** 상세 조회용 매핑 */
    public static AdminUserInfoResponse toInfoResponse(User u, UserInfoResponse userInfo) {
        return new AdminUserInfoResponse(
                u.getId(),
                u.getUsername(),
                userInfo.getProfileImage() != null ? userInfo.getProfileImage() : null,
                userInfo.getName(),
                userInfo.getDescription(),
                userInfo.getPhoneNumber(),
                u.getRole(),
                userInfo.getLevel(),
                userInfo.getNextLevelTotalExp(),
                userInfo.getCurrentTotalExp(),
                userInfo.getPoint()
        );
    }
}