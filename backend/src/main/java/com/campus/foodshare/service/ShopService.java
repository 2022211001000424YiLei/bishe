package com.campus.foodshare.service;

import com.campus.foodshare.entity.Shop;
import com.campus.foodshare.entity.User;
import com.campus.foodshare.repository.ShopRepository;
import com.campus.foodshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Transactional
    public Shop createShop(String name, String description, String imageUrl, String address, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Shop shop = new Shop();
        shop.setName(name);
        shop.setDescription(description);
        shop.setImageUrl(imageUrl);
        shop.setAddress(address);
        shop.setUser(user);

        return shopRepository.save(shop);
    }

    @Transactional
    public Shop saveShop(Long userId, String name, String description, String imageUrl, String address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        List<Shop> shops = shopRepository.findByUserId(userId);
        Shop shop = shops.isEmpty() ? new Shop() : shops.get(0);

        shop.setName(name);
        shop.setDescription(description);
        shop.setImageUrl(imageUrl);
        shop.setAddress(address);
        shop.setUser(user);
        shop.setUpdatedAt(LocalDateTime.now());

        return shopRepository.save(shop);
    }

    public List<Shop> getShopsByUserId(Long userId) {
        return shopRepository.findByUserId(userId);
    }

    public Shop getShopById(Long shopId) {
        return shopRepository.findById(shopId).orElse(null);
    }

    @Transactional
    public Shop updateShopById(Long shopId, Long userId, String name, String description, String imageUrl, String address) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));
        if (!shop.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权限修改");
        }
        if (name != null) shop.setName(name);
        if (description != null) shop.setDescription(description);
        if (imageUrl != null) shop.setImageUrl(imageUrl);
        if (address != null) shop.setAddress(address);
        shop.setUpdatedAt(LocalDateTime.now());
        return shopRepository.save(shop);
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }
}
