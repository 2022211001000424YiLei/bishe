package com.campus.foodshare.controller;

import com.campus.foodshare.dto.CommentResponse;
import com.campus.foodshare.dto.FoodResponse;
import com.campus.foodshare.dto.UserResponse;
import com.campus.foodshare.entity.Comment;
import com.campus.foodshare.entity.SiteSetting;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(users.map(UserResponse::fromEntity));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        if (user.getId().equals(userId)) {
            throw new RuntimeException("不能删除自己");
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/foods")
    public ResponseEntity<Page<FoodResponse>> getAllFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FoodResponse> foods = foodRepository.findAll(PageRequest.of(page, size))
                .map(FoodResponse::fromEntity);
        return ResponseEntity.ok(foods);
    }

    @DeleteMapping("/foods/{foodId}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long foodId) {
        foodRepository.deleteById(foodId);
        return ResponseEntity.ok().build();
    }

    // 待审核列表
    @GetMapping("/foods/pending")
    public ResponseEntity<Page<FoodResponse>> getPendingFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FoodResponse> foods = foodRepository.findByStatus("PENDING", PageRequest.of(page, size))
                .map(FoodResponse::fromEntity);
        return ResponseEntity.ok(foods);
    }

    // 审核通过
    @PostMapping("/foods/{foodId}/approve")
    public ResponseEntity<FoodResponse> approveFood(@PathVariable Long foodId) {
        return reviewFood(foodId, "APPROVED");
    }

    // 审核拒绝
    @PostMapping("/foods/{foodId}/reject")
    public ResponseEntity<FoodResponse> rejectFood(@PathVariable Long foodId) {
        return reviewFood(foodId, "REJECTED");
    }

    private ResponseEntity<FoodResponse> reviewFood(Long foodId, String status) {
        return ResponseEntity.ok(foodRepository.findById(foodId)
                .map(food -> {
                    food.setStatus(status);
                    return FoodResponse.fromEntity(foodRepository.save(food));
                })
                .orElseThrow(() -> new RuntimeException("美食不存在")));
    }

    // 获取网站设置
    @GetMapping("/settings")
    public ResponseEntity<Map<String, String>> getSettings() {
        Map<String, String> settings = new HashMap<>();
        siteSettingRepository.findBySettingKey("hero_title").ifPresent(s -> settings.put("heroTitle", s.getSettingValue()));
        siteSettingRepository.findBySettingKey("hero_subtitle").ifPresent(s -> settings.put("heroSubtitle", s.getSettingValue()));
        siteSettingRepository.findBySettingKey("hero_images").ifPresent(s -> settings.put("heroImages", s.getSettingValue()));
        return ResponseEntity.ok(settings);
    }

    // 更新网站设置
    @PutMapping("/settings")
    public ResponseEntity<Map<String, String>> updateSettings(@RequestBody Map<String, String> settings) {
        settings.forEach((key, value) -> {
            SiteSetting setting = siteSettingRepository.findBySettingKey(key)
                    .orElseGet(() -> {
                        SiteSetting s = new SiteSetting();
                        s.setSettingKey(key);
                        return s;
                    });
            setting.setSettingValue(value);
            setting.setUpdatedAt(LocalDateTime.now());
            siteSettingRepository.save(setting);
        });
        return ResponseEntity.ok(settings);
    }

    // 数据统计
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userRepository.count());
        stats.put("foodCount", foodRepository.count());
        stats.put("commentCount", commentRepository.count());
        stats.put("likeCount", likeRepository.count());

        // 最近7天新增数据
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        stats.put("newUsersThisWeek", userRepository.countByCreatedAtAfter(weekAgo));
        stats.put("newFoodsThisWeek", foodRepository.countByCreatedAtAfter(weekAgo));

        // 各类别美食数量
        stats.put("foodCountByCategory", foodRepository.countByStatusGroupByCategory());

        // 待审核数量
        stats.put("pendingCount", foodRepository.countByStatus("PENDING"));

        return ResponseEntity.ok(stats);
    }

    // 评论管理
    @GetMapping("/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Comment> comments = commentRepository.findAll(PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending()));
        return ResponseEntity.ok(comments.map(CommentResponse::fromEntity));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentRepository.deleteById(commentId);
        return ResponseEntity.ok().build();
    }
}
