package com.campus.foodshare.controller;

import com.campus.foodshare.entity.Shop;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    // 获取当前商家的店铺列表
    @GetMapping("/my")
    public ResponseEntity<?> getMyShop(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<Shop> shops = shopService.getShopsByUserId(user.getId());
        return ResponseEntity.ok(shops);
    }

    // 创建店铺
    @PostMapping
    public ResponseEntity<?> createShop(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        Shop shop = shopService.createShop(
                request.get("name"),
                request.get("description"),
                request.get("imageUrl"),
                request.get("address"),
                user.getId()
        );
        return ResponseEntity.ok(shop);
    }

    // 更新或创建店铺
    @PutMapping("/my")
    public ResponseEntity<?> saveMyShop(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        Shop shop = shopService.saveShop(
                user.getId(),
                request.get("name"),
                request.get("description"),
                request.get("imageUrl"),
                request.get("address")
        );
        return ResponseEntity.ok(shop);
    }

    // 获取所有店铺（用于用户浏览）
    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    // 根据ID获取店铺
    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {
        Shop shop = shopService.getShopById(id);
        if (shop == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shop);
    }

    // 更新指定店铺
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShop(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        Shop shop = shopService.updateShopById(id, user.getId(),
                request.get("name"),
                request.get("description"),
                request.get("imageUrl"),
                request.get("address")
        );
        return ResponseEntity.ok(shop);
    }
}
