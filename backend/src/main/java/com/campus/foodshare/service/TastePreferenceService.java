package com.campus.foodshare.service;

import com.campus.foodshare.dto.TastePreferenceRequest;
import com.campus.foodshare.dto.TastePreferenceResponse;
import com.campus.foodshare.entity.TastePreference;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.repository.TastePreferenceRepository;
import com.campus.foodshare.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TastePreferenceService {

    private final TastePreferenceRepository tastePreferenceRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public TastePreferenceResponse getByUserId(Long userId) {
        TastePreference pref = tastePreferenceRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultPreference(userId));
        return toResponse(pref);
    }

    public TastePreferenceResponse update(Long userId, TastePreferenceRequest request) {
        TastePreference pref = tastePreferenceRepository.findByUserId(userId)
            .orElseGet(() -> new TastePreference());

        User user = userRepository.findById(userId).orElseThrow();
        pref.setUser(user);
        pref.setSpicyLevel(request.getSpicyLevel() != null ? request.getSpicyLevel() : 3);
        pref.setSweetLevel(request.getSweetLevel() != null ? request.getSweetLevel() : 3);
        pref.setSaltyLevel(request.getSaltyLevel() != null ? request.getSaltyLevel() : 3);

        if (request.getPreferredCategories() != null && !request.getPreferredCategories().isEmpty()) {
            pref.setPreferredCategories(String.join(",", request.getPreferredCategories()));
        }

        pref.setPriceRange(request.getPriceRange() != null ? request.getPriceRange() : "ANY");
        pref.setDietaryRestriction(request.getDietaryRestriction() != null ? request.getDietaryRestriction() : "无");
        pref.setAdditionalNotes(request.getAdditionalNotes());
        pref.setUpdatedAt(LocalDateTime.now());

        return toResponse(tastePreferenceRepository.save(pref));
    }

    public String toProfileString(TastePreferenceResponse pref) {
        StringBuilder sb = new StringBuilder();
        sb.append("辣度偏好: ");
        if (pref.getSpicyLevel() != null) {
            sb.append(switch (pref.getSpicyLevel()) {
                case 1 -> "不吃辣";
                case 2 -> "微辣";
                case 3 -> "中辣";
                case 4 -> "较辣";
                case 5 -> "特辣";
                default -> "适中";
            });
        }
        sb.append("，甜度偏好: ");
        if (pref.getSweetLevel() != null) {
            sb.append(switch (pref.getSweetLevel()) {
                case 1 -> "不喜欢甜";
                case 2 -> "微甜";
                case 3 -> "适中";
                case 4 -> "较甜";
                case 5 -> "非常喜欢甜";
                default -> "适中";
            });
        }
        sb.append("，咸度偏好: ");
        if (pref.getSaltyLevel() != null) {
            sb.append(switch (pref.getSaltyLevel()) {
                case 1 -> "清淡";
                case 2 -> "微咸";
                case 3 -> "适中";
                case 4 -> "较咸";
                case 5 -> "重咸";
                default -> "适中";
            });
        }
        sb.append("。");

        if (pref.getPreferredCategories() != null && !pref.getPreferredCategories().isEmpty()) {
            sb.append("喜欢的分类: ").append(String.join("、", pref.getPreferredCategories())).append("。");
        }

        if (pref.getPriceRange() != null && !"ANY".equals(pref.getPriceRange())) {
            sb.append("价格区间: ").append(switch (pref.getPriceRange()) {
                case "LOW" -> "0-15元";
                case "MEDIUM" -> "15-30元";
                case "HIGH" -> "30元以上";
                default -> "不限";
            }).append("。");
        }

        if (pref.getDietaryRestriction() != null && !"无".equals(pref.getDietaryRestriction())) {
            sb.append("饮食限制: ").append(pref.getDietaryRestriction()).append("。");
        }

        if (pref.getAdditionalNotes() != null && !pref.getAdditionalNotes().isEmpty()) {
            sb.append("其他要求: ").append(pref.getAdditionalNotes());
        }

        return sb.toString();
    }

    private TastePreferenceResponse toResponse(TastePreference pref) {
        return TastePreferenceResponse.fromEntity(pref);
    }

    private TastePreference createDefaultPreference(Long userId) {
        TastePreference pref = new TastePreference();
        User user = userRepository.findById(userId).orElseThrow();
        pref.setUser(user);
        pref.setSpicyLevel(3);
        pref.setSweetLevel(3);
        pref.setSaltyLevel(3);
        pref.setPreferredCategories("主食,小吃,饮品,甜品");
        pref.setPriceRange("ANY");
        pref.setDietaryRestriction("无");
        pref.setUpdatedAt(LocalDateTime.now());
        return tastePreferenceRepository.save(pref);
    }
}
