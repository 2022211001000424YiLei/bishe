package com.campus.foodshare.dto;

import com.campus.foodshare.entity.Food;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FoodResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String location;
    private String category;
    private String tags;
    private Double price;
    private Integer likeCount;
    private Integer viewCount;
    private String status;
    private Long userId;
    private String username;
    private String userAvatar;
    private Boolean liked;
    private Boolean favorited;
    private LocalDateTime createdAt;
    private Long shopId;
    private String shopName;

    public static FoodResponse fromEntity(Food food) {
        FoodResponse response = new FoodResponse();
        response.setId(food.getId());
        response.setTitle(food.getTitle());
        response.setDescription(food.getDescription());
        response.setImageUrl(food.getImageUrl());
        response.setLocation(food.getLocation());
        response.setCategory(food.getCategory());
        response.setTags(food.getTags());
        response.setPrice(food.getPrice());
        response.setLikeCount(food.getLikeCount());
        response.setViewCount(food.getViewCount());
        response.setStatus(food.getStatus());
        response.setUserId(food.getUser().getId());
        response.setUsername(food.getUser().getUsername());
        response.setUserAvatar(food.getUser().getAvatar());
        response.setCreatedAt(food.getCreatedAt());
        if (food.getShop() != null) {
            response.setShopId(food.getShop().getId());
            response.setShopName(food.getShop().getName());
        }
        return response;
    }
}
