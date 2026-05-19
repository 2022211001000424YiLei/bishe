package com.campus.foodshare.repository;

import com.campus.foodshare.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndFoodId(Long userId, Long foodId);
    boolean existsByUserIdAndFoodId(Long userId, Long foodId);
    void deleteByUserIdAndFoodId(Long userId, Long foodId);
    void deleteByFoodId(Long foodId);
}
