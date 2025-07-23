package org.example.hugmeexp.domain.bookmark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.bookmark.dto.request.BookmarkRequest;
import org.example.hugmeexp.domain.bookmark.dto.response.BookmarkResponse;
import org.example.hugmeexp.domain.bookmark.entity.Bookmark;
import org.example.hugmeexp.domain.bookmark.exception.BookmarkNotFoundException;
import org.example.hugmeexp.domain.bookmark.exception.BookmarkUserNotFoundException;
import org.example.hugmeexp.domain.bookmark.service.BookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.example.hugmeexp.global.AWS.config.S3Config;
import org.example.hugmeexp.global.common.config.RedisConfig;
import org.example.hugmeexp.global.security.config.SecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookmarkController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, S3Config.class, RedisConfig.class})
        })
@AutoConfigureMockMvc
@DisplayName("BookmarkController 테스트")
@WithMockUser(username = "testuser@example.com", roles = {"USER"})
class BookmarkControllerTest {

    private static final String BASE_URL = "/api/v1/bookmark";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookmarkService bookmarkService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    private BookmarkRequest bookmarkRequest;

    @BeforeEach
    void setUp() {
        bookmarkRequest = BookmarkRequest.builder()
                .title("테스트 북마크")
                .link("https://example.com")
                .build();
    }

    @Nested
    @DisplayName("북마크 생성 테스트")
    class CreateBookmarkTest {

        @Test
        @DisplayName("성공")
        void createBookmark_Success() throws Exception {
            // Given
            doNothing().when(bookmarkService).createBookmark(anyString(), any(BookmarkRequest.class));

            // When & Then
            mockMvc.perform(post(BASE_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookmarkRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("북마크를 추가했습니다."));
        }

        @Test
        @DisplayName("실패 - 사용자를 찾을 수 없음")
        void createBookmark_UserNotFound() throws Exception {
            // Given
            doThrow(new BookmarkUserNotFoundException()).when(bookmarkService).createBookmark(anyString(), any(BookmarkRequest.class));

            // When & Then
            mockMvc.perform(post(BASE_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookmarkRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("북마크 전체 조회 테스트")
    class GetBookmarksTest {

        @Test
        @DisplayName("성공")
        void getBookmarks_Success() throws Exception {
            // Given
            Bookmark mockBookmark = Bookmark.builder()
                    .id(1L)
                    .title("테스트 북마크")
                    .link("https://example.com")
                    .build();
            List<BookmarkResponse> responses = Collections.singletonList(BookmarkResponse.fromEntity(mockBookmark));
            when(bookmarkService.getBookmarks(anyString())).thenReturn(responses);

            // When & Then
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].title").value("테스트 북마크"))
                    .andExpect(jsonPath("$.message").value("Bookmark status retrieved successfully"));
        }
    }

    @Nested
    @DisplayName("북마크 수정 테스트")
    class UpdateBookmarkTest {

        @Test
        @DisplayName("성공")
        void updateBookmark_Success() throws Exception {
            // Given
            doNothing().when(bookmarkService).updateBookmark(anyString(), anyLong(), any(BookmarkRequest.class));

            // When & Then
            mockMvc.perform(put(BASE_URL + "/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookmarkRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("북마크를 수정했습니다."));
        }

        @Test
        @DisplayName("실패 - 북마크를 찾을 수 없음")
        void updateBookmark_NotFound() throws Exception {
            // Given
            doThrow(new BookmarkNotFoundException()).when(bookmarkService).updateBookmark(anyString(), anyLong(), any(BookmarkRequest.class));

            // When & Then
            mockMvc.perform(put(BASE_URL + "/999")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookmarkRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("북마크 삭제 테스트")
    class DeleteBookmarkTest {

        @Test
        @DisplayName("성공")
        void deleteBookmark_Success() throws Exception {
            // Given
            doNothing().when(bookmarkService).deleteBookmark(anyString(), anyLong());

            // When & Then
            mockMvc.perform(delete(BASE_URL + "/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("북마크를 삭제했습니다."));
        }

        @Test
        @DisplayName("실패 - 북마크를 찾을 수 없음")
        void deleteBookmark_NotFound() throws Exception {
            // Given
            doThrow(new BookmarkNotFoundException()).when(bookmarkService).deleteBookmark(anyString(), anyLong());

            // When & Then
            mockMvc.perform(delete(BASE_URL + "/999")
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }
}