package com.campus.foodshare.repository;

import com.campus.foodshare.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByFoodId(Long foodId, Pageable pageable);
    long countByFoodId(Long foodId);
    void deleteByFoodId(Long foodId);
}
