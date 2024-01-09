package my.sns.controller;

import lombok.RequiredArgsConstructor;
import my.sns.common.ResultResponse;
import my.sns.dto.UserForm;
import my.sns.dto.request.UserJoinRequest;
import my.sns.dto.request.UserLoginRequest;
import my.sns.dto.response.AlarmResponse;
import my.sns.dto.response.UserJoinResponse;
import my.sns.dto.response.UserLoginResponse;
import my.sns.exception.CustomErrorCode;
import my.sns.exception.SnsApplicationException;
import my.sns.service.UserService;
import my.sns.util.ClassUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResultResponse<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        // join
        UserForm user = userService.join(request.getName(), request.getPassword());
        UserJoinResponse response = UserJoinResponse.fromUser(user);

        return ResultResponse.success(response);
    }

    @PostMapping("/login")
    public ResultResponse<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return ResultResponse.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public ResultResponse<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        UserForm user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserForm.class)
                .orElseThrow(() -> new SnsApplicationException(CustomErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed"));
        return ResultResponse.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }
}
