package com.campus.foodshare.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatResponse {
    private String response;
    private List<FoodResponse> foods;
}
