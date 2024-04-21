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

 Date: 21/04/2024 22:30:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_friends
-- ----------------------------
DROP TABLE IF EXISTS `user_friends`;
CREATE TABLE `user_friends`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL COMMENT '好友发起人id（主id）',
  `friendId` bigint(20) NOT NULL COMMENT '好友接收人id（好友id）',
  `relationshipStatus` tinyint(4) NULL DEFAULT 0 COMMENT '好友状态--0 申请中，1 通过，2 未通过',
  `isDelete` tinyint(4) NULL DEFAULT 0,
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
