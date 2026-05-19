package com.campus.foodshare.controller;

import com.campus.foodshare.entity.User;
import com.campus.foodshare.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/foods/{foodId}/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @PathVariable Long foodId,
            @AuthenticationPrincipal User user) {
        boolean liked = likeService.toggleLike(foodId, user.getId());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    }
