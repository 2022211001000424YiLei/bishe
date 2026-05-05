package com.campus.foodshare.repository;

import com.campus.foodshare.entity.TastePreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TastePreferenceRepository extends JpaRepository<TastePreference, Long> {
    Optional<TastePreference> findByUserId(Long userId);
}
