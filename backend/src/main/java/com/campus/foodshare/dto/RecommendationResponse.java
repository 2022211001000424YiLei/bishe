package com.campus.foodshare.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationResponse {
    private List<FoodResponse> recommendations;
    private String recommendationReason;
    private Integer count;
}
