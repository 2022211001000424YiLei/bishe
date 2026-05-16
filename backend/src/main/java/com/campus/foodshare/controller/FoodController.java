package com.campus.foodshare.controller;

import com.campus.foodshare.dto.FoodRequest;
import com.campus.foodshare.dto.FoodResponse;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.service.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @PostMapping
    public ResponseEntity<FoodResponse> createFood(
            @Valid @RequestBody FoodRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(foodService.createFood(request, user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodResponse> updateFood(
            @PathVariable Long id,
            @RequestBody FoodRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(foodService.updateFood(id, request, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        foodService.deleteFood(id, user.getId(), user.getRole());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFood(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        return ResponseEntity.ok(foodService.getFood(id, currentUserId));
    }

    @GetMapping
    public ResponseEntity<Page<FoodResponse>> getAllFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        return ResponseEntity.ok(foodService.getAllFoods(page, size, currentUserId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FoodResponse>> searchFoods(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        return ResponseEntity.ok(foodService.searchFoods(keyword, page, size, currentUserId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<FoodResponse>> getFoodsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        return ResponseEntity.ok(foodService.getFoodsByCategory(category, page, size, currentUserId));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<Page<FoodResponse>> getFoodsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        return ResponseEntity.ok(foodService.getFoodsByTag(tag, page, size, currentUserId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<FoodResponse>> getUserFoods(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        return ResponseEntity.ok(foodService.getUserFoods(userId, page, size, currentUserId));
    }

    @GetMapping("/top")
    public ResponseEntity<List<FoodResponse>> getTopFoods(
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        return ResponseEntity.ok(foodService.getTopFoods(limit, currentUserId));
    }
}
