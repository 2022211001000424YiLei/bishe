package com.campus.foodshare.service;

import com.campus.foodshare.dto.FoodResponse;
import com.campus.foodshare.entity.Favorite;
import com.campus.foodshare.entity.Food;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.repository.FavoriteRepository;
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
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleFavorite(Long foodId, Long userId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("美食不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        boolean exists = favoriteRepository.existsByUserIdAndFoodId(userId, foodId);

        if (exists) {
            favoriteRepository.deleteByUserIdAndFoodId(userId, foodId);
        } else {
            Favorite favorite = new Favorite();
            favorite.setFood(food);
            favorite.setUser(user);
            favoriteRepository.save(favorite);
        }

        return !exists;
    }

    public Page<FoodResponse> getUserFavorites(Long userId, int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Favorite> favorites = favoriteRepository.findByUserId(userId, pageable);
        return favorites.map(fav -> {
            FoodResponse response = FoodResponse.fromEntity(fav.getFood());
            if (currentUserId != null) {
                response.setFavorited(true);
            }
            return response;
        });
    }
}
