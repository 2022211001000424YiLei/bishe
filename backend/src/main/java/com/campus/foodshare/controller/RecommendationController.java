package com.campus.foodshare.controller;

import com.campus.foodshare.dto.*;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.service.FoodService;
import com.campus.foodshare.service.TastePreferenceService;
import com.campus.foodshare.service.TongyiQianwenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final TongyiQianwenService tongyiService;
    private final TastePreferenceService tastePreferenceService;
    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        TastePreferenceResponse tastePref = tastePreferenceService.getByUserId(user.getId());
        List<FoodResponse> allFoods = foodService.getAllApprovedFoods();

        String tasteProfile = tastePreferenceService.toProfileString(tastePref);
        String aiResponse = tongyiService.getRecommendations(tasteProfile, allFoods);

        List<Long> recommendedIds = parseRecommendedFoodIds(aiResponse, allFoods);
        List<FoodResponse> recommendations = foodService.getFoodsByIds(recommendedIds);

        RecommendationResponse response = new RecommendationResponse();
        response.setRecommendations(recommendations);
        response.setRecommendationReason(aiResponse);
        response.setCount(recommendations.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/taste-preferences")
    public ResponseEntity<TastePreferenceResponse> getTastePreferences(
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(tastePreferenceService.getByUserId(user.getId()));
    }

    @PutMapping("/taste-preferences")
    public ResponseEntity<TastePreferenceResponse> updateTastePreferences(
            @AuthenticationPrincipal User user,
            @RequestBody TastePreferenceRequest request) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(tastePreferenceService.update(user.getId(), request));
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @AuthenticationPrincipal User user,
            @RequestBody ChatRequest request) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        List<FoodResponse> allFoods = foodService.getAllApprovedFoods();
        String aiResponse = tongyiService.chat(request.getMessage(), allFoods);

        List<Long> recommendedIds = parseRecommendedFoodIds(aiResponse, allFoods);
        List<FoodResponse> foods = foodService.getFoodsByIds(recommendedIds);

        ChatResponse response = new ChatResponse();
        response.setResponse(aiResponse);
        response.setFoods(foods);

        return ResponseEntity.ok(response);
    }

    private List<Long> parseRecommendedFoodIds(String aiResponse, List<FoodResponse> allFoods) {
        if (aiResponse == null || aiResponse.isEmpty()) {
            return List.of();
        }

        List<Long> matchedIds = new java.util.ArrayList<>();
        for (FoodResponse food : allFoods) {
            if (aiResponse.contains(food.getTitle())) {
                matchedIds.add(food.getId());
                if (matchedIds.size() >= 5) break;
            }
        }

        if (matchedIds.isEmpty() && !allFoods.isEmpty()) {
            return allFoods.subList(0, Math.min(5, allFoods.size())).stream().map(FoodResponse::getId).toList();
        }

        return matchedIds;
    }
}
