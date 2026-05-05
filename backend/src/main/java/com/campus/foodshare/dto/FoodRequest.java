package com.campus.foodshare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FoodRequest {
    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;
    private String imageUrl;
    private String location;
    private String category;
    private Double price;
}
