package com.campus.foodshare.dto;

import com.campus.foodshare.entity.TastePreference;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
public class TastePreferenceResponse {
    private Long id;
    private Long userId;
    private Integer spicyLevel;
    private Integer sweetLevel;
    private Integer saltyLevel;
    private List<String> preferredCategories;
    private String priceRange;
    private String dietaryRestriction;
    private String additionalNotes;
    private LocalDateTime updatedAt;

    public static TastePreferenceResponse fromEntity(TastePreference pref) {
        TastePreferenceResponse response = new TastePreferenceResponse();
        response.setId(pref.getId());
        response.setUserId(pref.getUser().getId());
        response.setSpicyLevel(pref.getSpicyLevel());
        response.setSweetLevel(pref.getSweetLevel());
        response.setSaltyLevel(pref.getSaltyLevel());
        response.setPreferredCategories(parseCategories(pref.getPreferredCategories()));
        response.setPriceRange(pref.getPriceRange());
        response.setDietaryRestriction(pref.getDietaryRestriction());
        response.setAdditionalNotes(pref.getAdditionalNotes());
        response.setUpdatedAt(pref.getUpdatedAt());
        return response;
    }

    private static List<String> parseCategories(String categories) {
        if (categories == null || categories.isEmpty()) {
            return Arrays.asList("主食", "小吃", "饮品", "甜品");
        }
        return Arrays.asList(categories.split(","));
    }
}
