package my.sns.controller;

import lombok.RequiredArgsConstructor;
import my.sns.common.ResultResponse;
import my.sns.dto.PostForm;
import my.sns.dto.request.PostCreateRequest;
import my.sns.dto.request.PostModifyRequest;
import my.sns.dto.response.PostResponse;
import my.sns.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

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


    @PutMapping("/{postId}")
    public ResultResponse<PostResponse> modifyPost(@PathVariable Integer postId,
                                                   @RequestBody PostModifyRequest request,
                                                   Authentication authentication) {
        PostForm post = postService.modifyPost(request.getTitle(), request.getBody(), authentication.getName(), postId);
        return ResultResponse.success(PostResponse.fromPost(post));
    }


    @DeleteMapping("/{postId}")
    public ResultResponse<Void> deletePost(@PathVariable Integer postId, Authentication authentication) {
        postService.deletePost(authentication.getName(), postId);
        return ResultResponse.success();
    }


    @GetMapping
    public ResultResponse<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {
        return ResultResponse.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping
    public ResultResponse<Page<PostResponse>> myFeed(Pageable pageable, Authentication authentication) {
        return ResultResponse.success(postService.myFeed(authentication.getName(), pageable).map(PostResponse::fromPost));
    }

}
