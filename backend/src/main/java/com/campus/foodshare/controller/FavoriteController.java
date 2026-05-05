package com.campus.foodshare.controller;

import com.campus.foodshare.dto.FoodResponse;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/{foodId}")
    public ResponseEntity<Map<String, Boolean>> toggleFavorite(
            @PathVariable Long foodId,
            @AuthenticationPrincipal User user) {
        boolean favorited = favoriteService.toggleFavorite(foodId, user.getId());
        return ResponseEntity.ok(Map.of("favorited", favorited));
    }

    @GetMapping
    public ResponseEntity<Page<FoodResponse>> getUserFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(user.getId(), page, size, user.getId()));
    }
}
