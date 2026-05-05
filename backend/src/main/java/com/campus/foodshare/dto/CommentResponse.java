package com.campus.foodshare.dto;

import com.campus.foodshare.entity.Comment;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long foodId;
    private String foodTitle;
    private LocalDateTime createdAt;

    public static CommentResponse fromEntity(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setUserAvatar(comment.getUser().getAvatar());
        response.setFoodId(comment.getFood().getId());
        response.setFoodTitle(comment.getFood().getTitle());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
