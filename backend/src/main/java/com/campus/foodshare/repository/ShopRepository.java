package com.campus.foodshare.repository;

import com.campus.foodshare.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
