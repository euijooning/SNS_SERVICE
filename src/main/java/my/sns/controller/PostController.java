package my.sns.controller;

import lombok.RequiredArgsConstructor;
import my.sns.common.ResultResponse;
import my.sns.dto.request.PostCreateRequest;
import my.sns.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping()
    public ResultResponse<Void> createPost(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.createPost(request.getTitle(), request.getBody(), authentication.getName());

        return ResultResponse.success();

    }
}
