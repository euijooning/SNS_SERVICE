package my.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.sns.dto.UserForm;
import my.sns.dto.request.UserJoinRequest;
import my.sns.dto.request.UserLoginRequest;
import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.service.UserService;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    @Test
    @DisplayName("회원가입 테스트")
    public void t1() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.join(userName, password)).thenReturn(mock(UserForm.class));

        mockMvc.perform(post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 시 이미 가입된 userName인 경우 에러 발생")
    public void t2() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(CustomErrorCode.DUPLICATED_USER_NAME, ""));

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                )
                .andDo(print())
                .andExpect(status().is(CustomErrorCode.DUPLICATED_USER_NAME.getStatus().value()));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void t3() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenReturn("test_token");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("로그인 시 미가입된 userName인 경우 에러 발생")
    public void t4() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(CustomErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                )
                .andDo(print())
                .andExpect(status().is(CustomErrorCode.USER_NOT_FOUND.getStatus().value()));
    }


    @Test
    @DisplayName("로그인 시 password가 다른 경우 에러 발생")
    public void t5() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(CustomErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                )
                .andDo(print())
                .andExpect(status().is(CustomErrorCode.INVALID_PASSWORD.getStatus().value()));
    }


    @Test
    @DisplayName("알람 기능 테스트")
    @WithMockUser
    void t6() throws Exception {
        when(userService.alarmList(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/users/alarm")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("알람 리스트 요청 시 - 실패 / 로그인 하지 않은 경우")
    @WithAnonymousUser
    void t7() throws Exception {
        when(userService.alarmList(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
