package com.campus.foodshare.service;

import com.campus.foodshare.dto.*;
import com.campus.foodshare.entity.*;
import com.campus.foodshare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public FoodResponse createFood(FoodRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Food food = new Food();
        food.setTitle(request.getTitle());
        food.setDescription(request.getDescription());
        food.setImageUrl(request.getImageUrl());
        food.setLocation(request.getLocation());
        food.setCategory(request.getCategory());
        food.setTags(request.getTags());
        food.setPrice(request.getPrice());
        food.setUser(user);

        if (request.getShopId() != null) {
            Shop shop = shopRepository.findById(request.getShopId())
                    .orElseThrow(() -> new RuntimeException("店铺不存在"));
            food.setShop(shop);
        }

        return FoodResponse.fromEntity(foodRepository.save(food));
    }

    @Transactional
    public FoodResponse updateFood(Long foodId, FoodRequest request, Long userId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("美食不存在"));

        if (!food.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权限修改");
        }

        if (request.getTitle() != null) food.setTitle(request.getTitle());
        if (request.getDescription() != null) food.setDescription(request.getDescription());
        if (request.getImageUrl() != null) food.setImageUrl(request.getImageUrl());
        if (request.getLocation() != null) food.setLocation(request.getLocation());
        if (request.getCategory() != null) food.setCategory(request.getCategory());
        if (request.getTags() != null) food.setTags(request.getTags());
        if (request.getPrice() != null) food.setPrice(request.getPrice());

        return FoodResponse.fromEntity(foodRepository.save(food));
    }

    @Transactional
    public void deleteFood(Long foodId, Long userId, String role) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("美食不存在"));

        // 非管理员需要检查权限
        if (!"ADMIN".equals(role) && !food.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权限删除");
        }

        // 删除关联的评论、点赞、收藏
        commentRepository.deleteByFoodId(foodId);
        likeRepository.deleteByFoodId(foodId);
        favoriteRepository.deleteByFoodId(foodId);

        foodRepository.delete(food);
    }

    public FoodResponse getFood(Long foodId, Long currentUserId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("美食不存在"));

        // 增加浏览量
        food.setViewCount(food.getViewCount() + 1);
        foodRepository.save(food);

        FoodResponse response = FoodResponse.fromEntity(food);

        if (currentUserId != null) {
            response.setLiked(likeRepository.existsByUserIdAndFoodId(currentUserId, foodId));
            response.setFavorited(favoriteRepository.existsByUserIdAndFoodId(currentUserId, foodId));
        }

        return response;
    }

    public Page<FoodResponse> getAllFoods(int page, int size, Long currentUserId) {
        // 综合排序：点赞数 + 收藏数 + 评论数
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount", "createdAt"));
        Page<Food> foods = foodRepository.findApproved(pageable);
        return foods.map(food -> {
            FoodResponse response = FoodResponse.fromEntity(food);
            if (currentUserId != null) {
                response.setLiked(likeRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
                response.setFavorited(favoriteRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
            }
            return response;
        });
    }

    public Page<FoodResponse> searchFoods(String keyword, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount", "createdAt"));
        Page<Food> foods = foodRepository.searchApproved(keyword, pageable);
        return foods.map(food -> {
            FoodResponse response = FoodResponse.fromEntity(food);
            if (currentUserId != null) {
                response.setLiked(likeRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
                response.setFavorited(favoriteRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
            }
            return response;
        });
    }

    public Page<FoodResponse> getFoodsByCategory(String category, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount", "createdAt"));
        Page<Food> foods = foodRepository.findApprovedByCategory(category, pageable);
        return foods.map(food -> {
            FoodResponse response = FoodResponse.fromEntity(food);
            if (currentUserId != null) {
                response.setLiked(likeRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
                response.setFavorited(favoriteRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
            }
            return response;
        });
    }

    public Page<FoodResponse> getFoodsByTag(String tag, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount", "createdAt"));
        Page<Food> foods = foodRepository.findApprovedByTag(tag, pageable);
        return foods.map(food -> {
            FoodResponse response = FoodResponse.fromEntity(food);
            if (currentUserId != null) {
                response.setLiked(likeRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
                response.setFavorited(favoriteRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
            }
            return response;
        });
    }

    public Page<FoodResponse> getUserFoods(Long userId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Food> foods = foodRepository.findByUserId(userId, pageable);
        return foods.map(food -> {
            FoodResponse response = FoodResponse.fromEntity(food);
            if (currentUserId != null) {
                response.setLiked(likeRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
                response.setFavorited(favoriteRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
            }
            return response;
        });
    }

    public List<FoodResponse> getTopFoods(int limit, Long currentUserId) {
        List<Food> foods = foodRepository.findTopByLikesApproved(PageRequest.of(0, limit));
        return foods.stream().map(food -> {
            FoodResponse response = FoodResponse.fromEntity(food);
            if (currentUserId != null) {
                response.setLiked(likeRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
                response.setFavorited(favoriteRepository.existsByUserIdAndFoodId(currentUserId, food.getId()));
            }
            return response;
        }).toList();
    }

    public List<FoodResponse> getAllApprovedFoods() {
        List<Food> foods = foodRepository.findApprovedAll();
        return foods.stream().map(FoodResponse::fromEntity).toList();
    }

    public List<FoodResponse> getFoodsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return foodRepository.findAllById(ids).stream().map(FoodResponse::fromEntity).toList();
    }
}
