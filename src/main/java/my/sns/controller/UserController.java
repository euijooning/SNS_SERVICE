package my.sns.controller;

import lombok.RequiredArgsConstructor;
import my.sns.common.ResultResponse;
import my.sns.dto.UserDto;
import my.sns.dto.request.UserJoinRequest;
import my.sns.dto.response.UserJoinResponse;
import my.sns.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResultResponse<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        // join
        UserDto user = userService.join(request.getUserName(), request.getPassword());
        UserJoinResponse response = UserJoinResponse.fromUser(user);

        return ResultResponse.success(response);
    }
}
