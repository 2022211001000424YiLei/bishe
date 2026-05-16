-- ============================================
-- 校园美食分享系统 数据库初始化脚本
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS food_share DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE food_share;

-- ============================================
-- 1. 用户表 (users)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色: USER-普通用户, MERCHANT-商家, ADMIN-管理员',
    avatar VARCHAR(200) COMMENT '头像URL',
    bio VARCHAR(500) COMMENT '个人简介',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 美食表 (foods)
-- ============================================
CREATE TABLE IF NOT EXISTS foods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '美食ID',
    title VARCHAR(100) NOT NULL COMMENT '美食标题',
    description TEXT COMMENT '美食描述',
    image_url VARCHAR(500) COMMENT '图片URL',
    location VARCHAR(100) COMMENT '地点',
    category VARCHAR(50) COMMENT '分类',
    tags VARCHAR(200) COMMENT '标签',
    price DOUBLE COMMENT '价格（用户主观定价）',
    rating DOUBLE DEFAULT 0.0 COMMENT '评分',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    view_count INT DEFAULT 0 COMMENT '浏览数',
    user_id BIGINT NOT NULL COMMENT '发布用户ID',
    shop_id BIGINT COMMENT '关联店铺ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='美食表';

-- ============================================
-- 3. 评论表 (comments)
-- ============================================
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    content TEXT NOT NULL COMMENT '评论内容',
    food_id BIGINT NOT NULL COMMENT '美食ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ============================================
-- 4. 点赞表 (likes)
-- ============================================
CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    food_id BIGINT NOT NULL COMMENT '美食ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_food (user_id, food_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- ============================================
-- 5. 收藏表 (favorites)
-- ============================================
CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    food_id BIGINT NOT NULL COMMENT '美食ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_food (user_id, food_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================
-- 6. 口味偏好表 (taste_preferences)
-- ============================================
CREATE TABLE IF NOT EXISTS taste_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '偏好ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    spicy_level INT DEFAULT 3 COMMENT '辣度偏好: 1-5',
    sweet_level INT DEFAULT 3 COMMENT '甜度偏好: 1-5',
    salty_level INT DEFAULT 3 COMMENT '咸度偏好: 1-5',
    preferred_categories VARCHAR(500) COMMENT '偏好分类',
    price_range VARCHAR(20) DEFAULT 'ANY' COMMENT '价格范围: LOW/MEDIUM/HIGH/ANY',
    dietary_restriction VARCHAR(50) DEFAULT '无' COMMENT '饮食限制',
    additional_notes VARCHAR(500) COMMENT '备注',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='口味偏好表';

-- ============================================
-- 7. 店铺表 (shops)
-- ============================================
CREATE TABLE IF NOT EXISTS shops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '店铺ID',
    name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    description TEXT COMMENT '店铺简介',
    image_url VARCHAR(500) COMMENT '店铺图片URL',
    address VARCHAR(200) COMMENT '店铺地址',
    user_id BIGINT NOT NULL COMMENT '所属商家用户ID',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-营业中, INACTIVE-歇业',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺表';

-- ============================================
-- 说明：管理员账号由后端 DataInitializer 自动创建
-- 用户名: admin
-- 密码: admin123
-- ============================================
