package my.sns.controller;

import lombok.RequiredArgsConstructor;
import my.sns.common.ResultResponse;
import my.sns.dto.PostForm;
import my.sns.dto.request.PostCommentRequest;
import my.sns.dto.request.PostCreateRequest;
import my.sns.dto.request.PostModifyRequest;
import my.sns.dto.response.CommentResponse;
import my.sns.dto.response.PostResponse;
import my.sns.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResultResponse<Page<PostResponse>> list(Pageable pageable) {
        return ResultResponse.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public ResultResponse<Page<PostResponse>> myFeed(Pageable pageable, Authentication authentication) {
        return ResultResponse.success(postService.myFeed(authentication.getName(), pageable).map(PostResponse::fromPost));
    }


    @PostMapping("/{postId}/likes")
    public ResultResponse<Void> like(@PathVariable Integer postId, Authentication authentication) {
        postService.like(postId, authentication.getName());
        return ResultResponse.success();
    }

    @GetMapping("/{postId}/likes")
    public ResultResponse<Long> getLikes(@PathVariable Integer postId, Authentication authentication) {
        return ResultResponse.success(postService.getLikeCount(postId));
    }

    @PostMapping("/{postId}/comments")
    public ResultResponse<Void> comment(@PathVariable Integer postId,
                                        @RequestBody PostCommentRequest request,
                                        Authentication authentication) {
        postService.comment(postId, authentication.getName(), request.getComment());
        return ResultResponse.success();
    }

    @GetMapping("/{postId}/comments")
    public ResultResponse<Page<CommentResponse>> getComments(Pageable pageable,
                                                             @PathVariable Integer postId) {
        return ResultResponse.success(postService.getComments(postId, pageable).map(CommentResponse::fromComment));
    }
}
