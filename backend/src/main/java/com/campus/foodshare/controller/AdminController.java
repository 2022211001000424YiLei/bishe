package com.campus.foodshare.controller;

import com.campus.foodshare.dto.CommentResponse;
import com.campus.foodshare.dto.FoodResponse;
import com.campus.foodshare.dto.UserResponse;
import com.campus.foodshare.entity.Comment;
import com.campus.foodshare.entity.Shop;
import com.campus.foodshare.entity.SiteSetting;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.repository.*;
import com.campus.foodshare.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final ShopRepository shopRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final FoodService foodService;

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
    public ResponseEntity<Void> deleteFood(@PathVariable Long foodId, @AuthenticationPrincipal User user) {
        foodService.deleteFood(foodId, user.getId(), user.getRole());
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

    // ========== 商家管理 ==========

    // 获取所有商家（角色为MERCHANT的用户及其店铺）
    @GetMapping("/merchants")
    public ResponseEntity<?> getMerchants() {
        List<User> merchants = userRepository.findByRole("MERCHANT");
        List<Map<String, Object>> result = merchants.stream().map(merchant -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", merchant.getId());
            map.put("username", merchant.getUsername());
            map.put("email", merchant.getEmail());
            map.put("createdAt", merchant.getCreatedAt());
            List<Shop> shops = shopRepository.findByUserId(merchant.getId());
            map.put("shops", shops);
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }

    // 修改用户角色
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        String role = request.get("role");
        if (role == null || (!role.equals("USER") && !role.equals("MERCHANT") && !role.equals("ADMIN"))) {
            throw new RuntimeException("无效的角色");
        }
        userRepository.findById(userId).ifPresent(user -> {
            user.setRole(role);
            userRepository.save(user);
        });
        return ResponseEntity.ok().build();
    }

    // 删除店铺
    @DeleteMapping("/shops/{shopId}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long shopId) {
        shopRepository.deleteById(shopId);
        return ResponseEntity.ok().build();
    }
}
