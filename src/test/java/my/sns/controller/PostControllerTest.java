package my.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.sns.dto.PostForm;
import my.sns.dto.request.PostCreateRequest;
import my.sns.dto.request.PostModifyRequest;
import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.fixture.PostEntityFixture;
import my.sns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;


    @DisplayName("포스트 작성 성공")
    @Test
    @WithMockUser
    void t1() throws Exception {

        String title = "title";
        String body = "body";


        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("포스트 작성 실패 - 로그인 하지 않은 경우")
    @Test
    @WithAnonymousUser // 익명의 유저인 경우
    void t2() throws Exception {

        String title = "title";
        String body = "body";

        // 로그인 하지 않은 경우

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @DisplayName("포스트 삭제 성공")
    @Test
    @WithMockUser
    void t3() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("포스트 삭제 실패 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void t4() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @DisplayName("포스트 삭제 실패 - 글 작성자와 삭제 요청자 불일치")
    @WithMockUser
    @Test
    void t5() throws Exception {
        doThrow(new SnsApplicationException(CustomErrorCode.INVALID_PERMISSION)).when(postService).deletePost(any(), any());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @DisplayName("포스트 삭제 실패 - 삭제하려는 글이 없음")
    @WithMockUser
    @Test
    void t6() throws Exception {
        doThrow(new SnsApplicationException(CustomErrorCode.POST_NOT_FOUND)).when(postService).deletePost(any(), any());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }


    @WithMockUser
    @DisplayName("포스트 수정 성공")
    @Test
    void t7() throws Exception{
        String title = "title";
        String body = "body";

        when(postService.modifyPost(eq(title), eq(body), any(), any()))
                .thenReturn(PostForm.fromEntity(PostEntityFixture.get("userName", 1, 1)));

        mockMvc.perform(put("/api/v1/posts/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @DisplayName("포스트 수정 실패 - 로그인 하지 않은 경우의 요청")
    @Test
    void t8() throws Exception{
        String title = "title";
        String body = "body";

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @WithMockUser
    @DisplayName("포스트 수정 실패 - 요청자와 글 작성자가 일치하지 않은 경우")
    @Test
    void t9() throws Exception{
        String title = "title";
        String body = "body";

        doThrow(new SnsApplicationException(CustomErrorCode.INVALID_PERMISSION)).when(postService).modifyPost(eq(title), eq(body), any(), eq(1));

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @DisplayName("포스트 수정 실패 - 수정하려는 해당 글이 없는 경우")
    @Test
    void t10() throws Exception{
        String title = "title";
        String body = "body";

        doThrow(new SnsApplicationException(CustomErrorCode.POST_NOT_FOUND)).when(postService).modifyPost(eq(title), eq(body), any(), eq(1));

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }


    @DisplayName("피드 목록 조회 성공")
    @Test
    @WithMockUser
    void t11() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("피드 목록 조회 실패 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void t12() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @DisplayName("나의 피드 목록 조회 성공")
    @Test
    @WithMockUser
    void t13() throws Exception {
        when(postService.myFeed(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("나의 피드 목록 조회 실패 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void t14() throws Exception {
        when(postService.myFeed(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());

    }
}
