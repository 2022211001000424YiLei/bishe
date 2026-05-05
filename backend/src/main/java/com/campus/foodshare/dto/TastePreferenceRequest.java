package com.campus.foodshare.dto;

import lombok.Data;
import java.util.List;

@Data
public class TastePreferenceRequest {
    private Integer spicyLevel;
    private Integer sweetLevel;
    private Integer saltyLevel;
    private List<String> preferredCategories;
    private String priceRange;
    private String dietaryRestriction;
    private String additionalNotes;
}
