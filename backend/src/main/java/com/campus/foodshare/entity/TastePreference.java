package com.campus.foodshare.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "taste_preferences")
public class TastePreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "spicy_level")
    private Integer spicyLevel = 3;

    @Column(name = "sweet_level")
    private Integer sweetLevel = 3;

    @Column(name = "salty_level")
    private Integer saltyLevel = 3;

    @Column(name = "preferred_categories", length = 500)
    private String preferredCategories = "主食,小吃,饮品,甜品";

    @Column(name = "price_range", length = 20)
    private String priceRange = "ANY";

    @Column(name = "dietary_restriction", length = 50)
    private String dietaryRestriction = "无";

    @Column(name = "additional_notes", length = 500)
    private String additionalNotes;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
