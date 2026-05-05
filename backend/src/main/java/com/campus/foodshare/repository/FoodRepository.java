package com.campus.foodshare.repository;

import com.campus.foodshare.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Page<Food> findByUserId(Long userId, Pageable pageable);

    Page<Food> findByStatus(String status, Pageable pageable);

    long countByStatus(String status);

    long countByCreatedAtAfter(LocalDateTime after);

    @Query("SELECT f.category, COUNT(f) FROM Food f WHERE f.status = 'APPROVED' GROUP BY f.category")
    List<Object[]> countByStatusGroupByCategory();

    @Query("SELECT f FROM Food f WHERE f.category = :category")
    Page<Food> findByCategory(@Param("category") String category, Pageable pageable);

    @Query("SELECT f FROM Food f WHERE f.title LIKE %:keyword% OR f.description LIKE %:keyword%")
    Page<Food> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT f FROM Food f ORDER BY f.likeCount DESC")
    List<Food> findTopByLikes(Pageable pageable);

    @Query("SELECT f FROM Food f ORDER BY f.createdAt DESC")
    Page<Food> findLatest(Pageable pageable);

    @Query("SELECT f FROM Food f WHERE f.status = 'APPROVED' ORDER BY f.createdAt DESC")
    Page<Food> findApproved(Pageable pageable);

    @Query("SELECT f FROM Food f WHERE f.status = 'APPROVED' ORDER BY f.createdAt DESC")
    List<Food> findApprovedAll();

    @Query("SELECT f FROM Food f WHERE f.status = 'APPROVED' ORDER BY f.likeCount DESC")
    List<Food> findTopByLikesApproved(Pageable pageable);

    @Query("SELECT f FROM Food f WHERE f.status = 'APPROVED' AND (f.title LIKE %:keyword% OR f.description LIKE %:keyword%)")
    Page<Food> searchApproved(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT f FROM Food f WHERE f.status = 'APPROVED' AND f.category = :category")
    Page<Food> findApprovedByCategory(@Param("category") String category, Pageable pageable);
}
