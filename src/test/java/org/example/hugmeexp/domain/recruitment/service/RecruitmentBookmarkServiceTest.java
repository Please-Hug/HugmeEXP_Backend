package org.example.hugmeexp.domain.recruitment.service;

import org.example.hugmeexp.domain.recruitment.dto.RecruitmentResponseDTO;
import org.example.hugmeexp.domain.recruitment.entity.Company;
import org.example.hugmeexp.domain.recruitment.entity.Recruitment;
import org.example.hugmeexp.domain.recruitment.entity.RecruitmentBookmark;
import org.example.hugmeexp.domain.recruitment.exception.DuplicateRecruitmentBookmarkException;
import org.example.hugmeexp.domain.recruitment.exception.RecruitmentBookmarkNotFoundException;
import org.example.hugmeexp.domain.recruitment.repository.RecruitmentBookmarkRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.exception.UserNotFoundException;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecruitmentBookmarkService 테스트")
public class RecruitmentBookmarkServiceTest {

    @Mock
    private RecruitmentBookmarkRepository recruitmentBookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecruitmentService recruitmentService;

    @InjectMocks
    private RecruitmentBookmarkService recruitmentBookmarkService;

    private User testUser;
    private Company testCompany;
    private Recruitment testRecruitment;
    private RecruitmentBookmark testBookmark;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .password("password123")
                .name("테스트유저")
                .phoneNumber("010-1234-5678")
                .build();
        ReflectionTestUtils.setField(testUser, "id", 1L);

        testCompany = Company.builder()
                .id(1L)
                .companyName("테스트 회사")
                .companyAddress("서울시 강남구")
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .establishmentDate(LocalDate.of(2020, 1, 1))
                .companyImageUrl("http://example.com/image.jpg")
                .companyDescription("테스트 회사 설명")
                .build();

        testRecruitment = Recruitment.builder()
                .id(1L)
                .title("테스트 채용공고")
                .company(testCompany)
                .education(1)
                .experienceMin(0)
                .experienceMax(3)
                .build();

        testBookmark = RecruitmentBookmark.builder()
                .id(1L)
                .user(testUser)
                .recruitment(testRecruitment)
                .build();
    }

    @Nested
    @DisplayName("즐겨찾기 등록 테스트")
    class AddBookmarkTest {

        @Test
        @DisplayName("정상적으로 즐겨찾기를 등록한다")
        void addBookmark_Success() {
            // Given
            Long userId = 1L;
            Long recruitmentId = 1L;

            given(recruitmentService.getRecruitmentById(recruitmentId)).willReturn(testRecruitment);
            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
            given(recruitmentBookmarkRepository.existsByUserAndRecruitment(testUser, testRecruitment)).willReturn(false);
            given(recruitmentBookmarkRepository.save(any(RecruitmentBookmark.class))).willReturn(testBookmark);

            // When & Then
            assertThatCode(() -> recruitmentBookmarkService.addBookmark(userId, recruitmentId))
                    .doesNotThrowAnyException();

            // Verify
            verify(recruitmentService).getRecruitmentById(recruitmentId);
            verify(userRepository).findById(userId);
            verify(recruitmentBookmarkRepository).existsByUserAndRecruitment(testUser, testRecruitment);
            verify(recruitmentBookmarkRepository).save(any(RecruitmentBookmark.class));
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 즐겨찾기 등록 시 예외가 발생한다")
        void addBookmark_UserNotFound_ThrowsException() {
            // Given
            Long userId = 999L;
            Long recruitmentId = 1L;

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> recruitmentBookmarkService.addBookmark(userId, recruitmentId))
                    .isInstanceOf(UserNotFoundException.class);

            // Verify
            verify(userRepository).findById(userId);
            verify(recruitmentService, never()).getRecruitmentById(any());
            verify(recruitmentBookmarkRepository, never()).existsByUserAndRecruitment(any(), any());
            verify(recruitmentBookmarkRepository, never()).save(any());
        }

        @Test
        @DisplayName("이미 등록된 즐겨찾기를 다시 등록하면 예외가 발생한다")
        void addBookmark_DuplicateBookmark_ThrowsException() {
            // Given
            Long userId = 1L;
            Long recruitmentId = 1L;

            given(recruitmentService.getRecruitmentById(recruitmentId)).willReturn(testRecruitment);
            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
            given(recruitmentBookmarkRepository.existsByUserAndRecruitment(testUser, testRecruitment)).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> recruitmentBookmarkService.addBookmark(userId, recruitmentId))
                    .isInstanceOf(DuplicateRecruitmentBookmarkException.class);

            // Verify
            verify(recruitmentService).getRecruitmentById(recruitmentId);
            verify(userRepository).findById(userId);
            verify(recruitmentBookmarkRepository).existsByUserAndRecruitment(testUser, testRecruitment);
            verify(recruitmentBookmarkRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("즐겨찾기 삭제 테스트")
    class RemoveBookmarkTest {

        @Test
        @DisplayName("정상적으로 즐겨찾기를 삭제한다")
        void removeBookmark_Success() {
            // Given
            Long userId = 1L;
            Long recruitmentId = 1L;

            given(recruitmentService.getRecruitmentById(recruitmentId)).willReturn(testRecruitment);
            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
            given(recruitmentBookmarkRepository.findByUserAndRecruitment(testUser, testRecruitment)).willReturn(Optional.of(testBookmark));

            // When & Then
            assertThatCode(() -> recruitmentBookmarkService.removeBookmark(userId, recruitmentId))
                    .doesNotThrowAnyException();

            // Verify
            verify(recruitmentService).getRecruitmentById(recruitmentId);
            verify(userRepository).findById(userId);
            verify(recruitmentBookmarkRepository).findByUserAndRecruitment(testUser, testRecruitment);
            verify(recruitmentBookmarkRepository).delete(testBookmark);
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 즐겨찾기 삭제 시 예외가 발생한다")
        void removeBookmark_UserNotFound_ThrowsException() {
            // Given
            Long userId = 999L;
            Long recruitmentId = 1L;

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> recruitmentBookmarkService.removeBookmark(userId, recruitmentId))
                    .isInstanceOf(UserNotFoundException.class);

            // Verify
            verify(userRepository).findById(userId);
            verify(recruitmentService, never()).getRecruitmentById(any());
            verify(recruitmentBookmarkRepository, never()).findByUserAndRecruitment(any(), any());
            verify(recruitmentBookmarkRepository, never()).delete(any());
        }

        @Test
        @DisplayName("존재하지 않는 즐겨찾기를 삭제하려고 할 때 예외가 발생한다")
        void removeBookmark_BookmarkNotFound_ThrowsException() {
            // Given
            Long userId = 1L;
            Long recruitmentId = 1L;

            given(recruitmentService.getRecruitmentById(recruitmentId)).willReturn(testRecruitment);
            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
            given(recruitmentBookmarkRepository.findByUserAndRecruitment(testUser, testRecruitment)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> recruitmentBookmarkService.removeBookmark(userId, recruitmentId))
                    .isInstanceOf(RecruitmentBookmarkNotFoundException.class);

            // Verify
            verify(recruitmentService).getRecruitmentById(recruitmentId);
            verify(userRepository).findById(userId);
            verify(recruitmentBookmarkRepository).findByUserAndRecruitment(testUser, testRecruitment);
            verify(recruitmentBookmarkRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("즐겨찾기 목록 조회 테스트")
    class GetRecruitmentBookmarksTest {

        @Test
        @DisplayName("정상적으로 즐겨찾기 목록을 조회한다")
        void getRecruitmentBookmarks_Success() {
            // Given
            Long userId = 1L;
            List<RecruitmentBookmark> bookmarks = new ArrayList<>();
            bookmarks.add(testBookmark);

            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
            given(recruitmentBookmarkRepository.findAllByUser(testUser)).willReturn(bookmarks);

            // When
            List<RecruitmentResponseDTO> result = recruitmentBookmarkService.getRecruitmentBookmarks(userId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(testRecruitment.getId());
            assertThat(result.get(0).getTitle()).isEqualTo(testRecruitment.getTitle());

            // Verify
            verify(userRepository).findById(userId);
            verify(recruitmentBookmarkRepository).findAllByUser(testUser);
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 즐겨찾기 목록 조회 시 예외가 발생한다")
        void getRecruitmentBookmarks_UserNotFound_ThrowsException() {
            // Given
            Long userId = 999L;

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> recruitmentBookmarkService.getRecruitmentBookmarks(userId))
                    .isInstanceOf(UserNotFoundException.class);

            // Verify
            verify(userRepository).findById(userId);
            verify(recruitmentBookmarkRepository, never()).findAllByUser(any());
        }

        @Test
        @DisplayName("즐겨찾기가 없는 경우 빈 목록을 반환한다")
        void getRecruitmentBookmarks_NoBookmarks_ReturnsEmptyList() {
            // Given
            Long userId = 1L;

            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
            given(recruitmentBookmarkRepository.findAllByUser(testUser)).willReturn(Collections.emptyList());

            // When
            List<RecruitmentResponseDTO> result = recruitmentBookmarkService.getRecruitmentBookmarks(userId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            // Verify
            verify(userRepository).findById(userId);
            verify(recruitmentBookmarkRepository).findAllByUser(testUser);
        }
    }
}
