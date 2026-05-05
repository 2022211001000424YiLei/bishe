package com.campus.foodshare.service;

import com.campus.foodshare.dto.FoodResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TongyiQianwenService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${tongyi.qianwen.api-key}")
    private String apiKey;

    @Value("${tongyi.qianwen.endpoint}")
    private String endpoint;

    @Value("${tongyi.qianwen.model}")
    private String model;

    public String getRecommendations(String tasteProfile, List<FoodResponse> availableFoods) {
        String prompt = buildRecommendationPrompt(tasteProfile, availableFoods);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("x-DashScope-OW", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);
            return parseRecommendationResponse(response.getBody());
        } catch (Exception e) {
            log.error("调用通义千问API失败", e);
            return "抱歉，AI推荐服务暂时不可用，请稍后再试。";
        }
    }

    private String buildRecommendationPrompt(String tasteProfile, List<FoodResponse> foods) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个校园美食推荐专家。根据用户的口味偏好，从以下美食列表中推荐最适合的美食。\n\n");
        sb.append("用户口味偏好：").append(tasteProfile).append("\n\n");
        sb.append("美食列表：\n");

        for (FoodResponse food : foods) {
            sb.append(String.format("- %s | 分类: %s | 价格: %.2f元 | 地点: %s | 描述: %s\n",
                food.getTitle(), food.getCategory(), food.getPrice(),
                food.getLocation(), food.getDescription() != null ? food.getDescription() : "暂无"));
        }

        sb.append("\n请根据用户偏好推荐最合适的3-5种美食，并简要说明推荐理由（每种美食一句话理由）。格式要求：先列出推荐列表，然后用一段话总结推荐理由。");
        return sb.toString();
    }

    private String parseRecommendationResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).path("message");
                return message.path("content").asText();
            }
            return "抱歉，未能获取到有效的推荐结果。";
        } catch (Exception e) {
            log.error("解析通义千问响应失败", e);
            return "抱歉，AI推荐服务暂时不可用，请稍后再试。";
        }
    }

    public String chat(String userMessage, List<FoodResponse> availableFoods) {
        String prompt = buildChatPrompt(userMessage, availableFoods);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        requestBody.put("temperature", 0.8);
        requestBody.put("max_tokens", 2000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("x-DashScope-OW", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);
            return parseRecommendationResponse(response.getBody());
        } catch (Exception e) {
            log.error("调用通义千问API失败", e);
            return "抱歉，AI服务暂时不可用，请稍后再试。";
        }
    }

    private String buildChatPrompt(String userMessage, List<FoodResponse> foods) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个校园美食推荐助手，正在和用户对话。用户会告诉你他想吃什么类型的美食，你需要根据校园美食列表来回答。\n\n");
        sb.append("【重要】你只能推荐以下列表中的美食，不要推荐列表以外的食物！\n\n");
        sb.append("校园美食列表：\n");

        for (FoodResponse food : foods) {
            sb.append(String.format("- %s | 分类: %s | 价格: %.2f元 | 地点: %s | 描述: %s\n",
                food.getTitle(), food.getCategory(), food.getPrice(),
                food.getLocation(), food.getDescription() != null ? food.getDescription() : "暂无"));
        }

        sb.append("\n用户说：").append(userMessage).append("\n\n");
        sb.append("请用友好的语气回答，先根据用户需求推荐1-3种美食，然后简单说明推荐理由。如果用户的需求比较模糊，可以多问一句比如口味、预算等。请用简洁的方式回复。");
        return sb.toString();
    }
}
