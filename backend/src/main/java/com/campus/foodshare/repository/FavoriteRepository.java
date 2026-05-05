package com.campus.foodshare.repository;

import com.campus.foodshare.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndFoodId(Long userId, Long foodId);
    boolean existsByUserIdAndFoodId(Long userId, Long foodId);
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
    void deleteByUserIdAndFoodId(Long userId, Long foodId);
    void deleteByFoodId(Long foodId);
}
