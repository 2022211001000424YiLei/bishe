package com.campus.foodshare.service;

import com.campus.foodshare.entity.Food;
import com.campus.foodshare.entity.Like;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.repository.FoodRepository;
import com.campus.foodshare.repository.LikeRepository;
import com.campus.foodshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(Long foodId, Long userId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("美食不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        boolean exists = likeRepository.existsByUserIdAndFoodId(userId, foodId);

        if (exists) {
            likeRepository.deleteByUserIdAndFoodId(userId, foodId);
            food.setLikeCount(Math.max(0, food.getLikeCount() - 1));
        } else {
            Like like = new Like();
            like.setFood(food);
            like.setUser(user);
            likeRepository.save(like);
            food.setLikeCount(food.getLikeCount() + 1);
        }

        foodRepository.save(food);
        return !exists;
    }
}
