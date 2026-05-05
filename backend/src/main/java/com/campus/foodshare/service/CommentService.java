package com.campus.foodshare.service;

import com.campus.foodshare.dto.CommentResponse;
import com.campus.foodshare.entity.Comment;
import com.campus.foodshare.entity.Food;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.repository.CommentRepository;
import com.campus.foodshare.repository.FoodRepository;
import com.campus.foodshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(Long foodId, String content, Long userId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("美食不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setFood(food);
        comment.setUser(user);

        return CommentResponse.fromEntity(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权限删除");
        }

        commentRepository.delete(comment);
    }

    public Page<CommentResponse> getFoodComments(Long foodId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentRepository.findByFoodId(foodId, pageable);
        return comments.map(CommentResponse::fromEntity);
    }
}
