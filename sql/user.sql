/*
 Navicat Premium Data Transfer

 Source Server         : zzj
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : xztx

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 16/02/2024 15:30:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userProfile` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '个人简介',
  `avatarUrl` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别',
  `userAccount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `userPassword` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `userStatus` int(11) NOT NULL DEFAULT 0 COMMENT '0-表示正常',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `userRole` int(11) NOT NULL DEFAULT 0 COMMENT '0-普通，1-管理员',
  `tags` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签列表 json',
  `phone` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '热爱编程，头发还健在，哈哈哈哈哈，平时听听音乐', 'https://zzj-img.oss-cn-hangzhou.aliyuncs.com/2024/02.jpg', 0, 'zzjzzj', '2970a1691c8ef07f40ddbe6f7b18662f', 'zzjzzj', '123', 0, '2024-01-29 20:48:23', '2024-01-29 20:48:23', 0, 1, '[\"java\",\"秋招\"]', '123');
INSERT INTO `user` VALUES (2, '热爱编程，头发还健在，哈哈哈哈哈，平时听听音乐', 'https://zzj-img.oss-cn-hangzhou.aliyuncs.com/2024/hdjpg.jpg', 0, 'zzjny', '2970a1691c8ef07f40ddbe6f7b18662f', 'zzjny', '123', 0, '2024-01-29 20:48:23', '2024-01-29 20:48:23', 0, 1, '[\"c++\",\"春招\"]', '123');
SET FOREIGN_KEY_CHECKS = 1;
