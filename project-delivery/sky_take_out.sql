/*
 Navicat Premium Data Transfer

 Source Server         : cy
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : sky_take_out

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 03/06/2024 00:11:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型   1 菜品分类 2 套餐分类',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '分类名称',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '顺序',
  `status` int(11) NULL DEFAULT NULL COMMENT '分类状态 0:禁用，1:启用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_category_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '菜品及套餐分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (2, 1, '经典畅销', 2, 1, '2024-05-06 00:35:39', '2024-05-06 00:47:41', 1, 1);
INSERT INTO `category` VALUES (3, 1, '湖南小炒', 3, 1, '2024-05-06 00:35:50', '2024-05-06 00:47:43', 1, 1);
INSERT INTO `category` VALUES (4, 1, '家常热菜', 4, 1, '2024-05-06 00:36:01', '2024-05-06 00:47:45', 1, 1);
INSERT INTO `category` VALUES (5, 1, '酱味卤菜', 5, 1, '2024-05-06 00:36:17', '2024-05-06 00:47:47', 1, 1);
INSERT INTO `category` VALUES (6, 1, '米饭', 8, 1, '2024-05-06 00:36:27', '2024-05-06 01:04:16', 1, 1);
INSERT INTO `category` VALUES (7, 1, '酒饮', 9, 1, '2024-05-06 00:36:44', '2024-05-06 01:04:22', 1, 1);
INSERT INTO `category` VALUES (8, 2, '双人套餐', 7, 1, '2024-05-06 00:37:25', '2024-05-06 01:04:10', 1, 1);
INSERT INTO `category` VALUES (9, 2, '单人套餐', 6, 1, '2024-05-06 01:03:49', '2024-05-06 01:04:24', 1, 1);

-- ----------------------------
-- Table structure for dish
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '菜品名称',
  `category_id` bigint(20) NOT NULL COMMENT '菜品分类id',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '菜品价格',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '图片',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '描述信息',
  `status` int(11) NULL DEFAULT 1 COMMENT '0 停售 1 起售',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_dish_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 73 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '菜品' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dish
-- ----------------------------
INSERT INTO `dish` VALUES (1, '钢盆鱿鱼', 2, 39.00, 'http://47.113.191.68:9000/reggie/钢盆鱿鱼.png', '鱿鱼丝500克、小红椒圈110克、韭菜段150克、粗蒜40克、黄辣椒粉5克。外卖与堂食的食材标准一致、因考虑打包因素制作略有调整！', 1, '2024-05-06 00:50:09', '2024-05-06 01:03:07', 1, 1);
INSERT INTO `dish` VALUES (2, '辣椒炒肉', 2, 26.00, 'http://47.113.191.68:9000/reggie/辣椒炒肉.png', '瘦肉片150克 肥肉片100克 青辣椒200克\n', 1, '2024-05-06 00:50:46', '2024-05-06 01:03:05', 1, 1);
INSERT INTO `dish` VALUES (3, '酸辣巴沙鱼片', 2, 39.00, 'http://47.113.191.68:9000/reggie/酸辣鱼片.png', '巴沙鱼肉430克 酸菜130克（温馨提示：酸菜预制时带有小米辣，菜品不可完全免辣）', 1, '2024-05-06 00:51:30', '2024-05-06 01:03:03', 1, 1);
INSERT INTO `dish` VALUES (4, '攸县血鸭', 3, 32.00, 'http://47.113.191.68:9000/reggie/攸县血鸭.png', '去骨鸭肉丁350g、碎红椒40克、小米椒30克\n', 1, '2024-05-06 00:52:16', '2024-05-06 01:02:37', 1, 1);
INSERT INTO `dish` VALUES (5, '酸溜土豆丝', 3, 14.00, 'http://47.113.191.68:9000/reggie/酸溜土豆丝.png', '土豆丝450克', 1, '2024-05-06 00:52:45', '2024-05-06 01:02:20', 1, 1);
INSERT INTO `dish` VALUES (6, '农家一碗香', 3, 24.00, 'http://47.113.191.68:9000/reggie/农家一碗香.png', '肥肉40克、瘦肉80克、煎鸡蛋150克、辣椒碎70克\n', 1, '2024-05-06 00:53:21', '2024-05-06 01:01:31', 1, 1);
INSERT INTO `dish` VALUES (7, '虾仁玉米粒', 3, 19.00, 'http://47.113.191.68:9000/reggie/虾仁玉米粒.png', '三鲜玉米粒330克、虾仁6-8个25克\n', 1, '2024-05-06 00:54:01', '2024-05-06 01:01:29', 1, 1);
INSERT INTO `dish` VALUES (8, '糖醋排骨', 4, 29.00, 'http://47.113.191.68:9000/reggie/糖醋排骨.png', '炸排骨180克、炸土豆100克、糖醋汁120克 (菜品的糖醋汁和排骨是裹在一起的哦)\n', 1, '2024-05-06 00:54:54', '2024-05-06 01:01:27', 1, 1);
INSERT INTO `dish` VALUES (9, '爆炒牛蛙', 4, 35.00, 'http://47.113.191.68:9000/reggie/爆炒牛蛙.png', '牛蛙块300克、碎红椒50克、小米椒30克、韭菜段50克、鲜紫苏5克\n', 1, '2024-05-06 00:55:37', '2024-05-06 01:01:26', 1, 1);
INSERT INTO `dish` VALUES (10, '热卤四合一', 5, 37.00, 'http://47.113.191.68.162:9000/reggie/热卤四合一.png', '卤牛腱子40克、卤猪耳80克、卤牛大肚30克、素菜100克\n', 1, '2024-05-06 00:56:22', '2024-05-06 01:01:24', 1, 1);
INSERT INTO `dish` VALUES (11, '热卤鸭锁骨', 5, 9.00, 'http://47.113.191.68:9000/reggie/热卤鸭锁骨.png', '卤鸭锁骨150克', 1, '2024-05-06 00:57:22', '2024-05-06 01:01:21', 1, 1);
INSERT INTO `dish` VALUES (12, '米饭', 6, 1.50, 'http://47.113.191.68:9000/reggie/ee04a05a-1230-46b6-8ad5-1a95b140fff3.png', '约320克', 1, '2024-05-06 00:58:19', '2024-05-06 01:01:17', 1, 1);
INSERT INTO `dish` VALUES (13, '矿泉水', 7, 2.00, 'http://47.113.191.68:9000/reggie/矿泉水.png', '', 1, '2024-05-06 00:58:50', '2024-05-06 01:01:15', 1, 1);
INSERT INTO `dish` VALUES (14, '可乐', 7, 2.50, 'http://47.113.191.68:9000/reggie/可乐.png', '', 1, '2024-05-06 00:59:17', '2024-05-06 01:01:13', 1, 1);
INSERT INTO `dish` VALUES (15, '王老吉', 7, 3.00, 'http://47.113.191.68:9000/reggie/00874a5e-0df2-446b-8f69-a30eb7d88ee8.png', '', 1, '2024-05-06 00:59:55', '2024-05-06 01:01:11', 1, 1);
INSERT INTO `dish` VALUES (16, '啤酒', 7, 5.00, 'http://47.113.191.68:9000/reggie/c99e0aab-3cb7-4eaa-80fd-f47d4ffea694.png', '', 1, '2024-05-06 01:00:18', '2024-05-06 01:00:59', 1, 1);
INSERT INTO `dish` VALUES (17, '小炒青菜', 4, 9.00, 'http://47.113.191.68:9000/reggie/青菜.png', '', 1, '2024-05-06 01:19:42', '2024-05-19 23:39:17', 1, 1);

-- ----------------------------
-- Table structure for dish_flavor
-- ----------------------------
DROP TABLE IF EXISTS `dish_flavor`;
CREATE TABLE `dish_flavor`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dish_id` bigint(20) NOT NULL COMMENT '菜品',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '口味名称',
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '口味数据list',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '菜品口味关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dish_flavor
-- ----------------------------
INSERT INTO `dish_flavor` VALUES (1, 11, '辣度', '[\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (3, 15, '温度', '[\"常温\",\"少冰\"]');
INSERT INTO `dish_flavor` VALUES (4, 16, '温度', '[\"常温\",\"少冰\"]');
INSERT INTO `dish_flavor` VALUES (5, 14, '温度', '[\"常温\",\"少冰\"]');
INSERT INTO `dish_flavor` VALUES (6, 1, '辣度', '[\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (7, 4, '辣度', '[\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (8, 3, '辣度', '[\"微辣\",\"中辣\",\"重辣\"]');
INSERT INTO `dish_flavor` VALUES (9, 2, '辣度', '[\"微辣\",\"中辣\",\"重辣\"]');

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '姓名',
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '性别',
  `id_number` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '身份证号',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态 0:禁用，1:启用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '员工信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (1, '管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', ' ', '1', '110101199001010047', 1, '2022-02-15 15:51:20', '2024-05-02 21:18:31', 10, 1);
INSERT INTO `employee` VALUES (2, '员工1', '20202671', 'e10adc3949ba59abbe56e057f20f883e', ' ', '1', '430721200111042577', 1, '2024-03-23 22:46:47', '2024-05-01 22:22:54', 1, 1);

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '名字',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '图片',
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `dish_id` bigint(20) NULL DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint(20) NULL DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '口味',
  `number` int(11) NOT NULL DEFAULT 1 COMMENT '数量',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_detail
-- ----------------------------
INSERT INTO `order_detail` VALUES (1, '酸辣巴沙鱼片', 'http://47.113.191.68:9000/reggie/酸辣鱼片.png', 1, 3, NULL, '微辣', 1, 39.00);
INSERT INTO `order_detail` VALUES (2, '酸辣巴沙鱼片', 'http://47.113.191.68:9000/reggie/酸辣鱼片.png', 2, 3, NULL, '微辣', 1, 39.00);
INSERT INTO `order_detail` VALUES (3, '钢盆鱿鱼', 'http://47.113.191.68:9000/reggie/钢盆鱿鱼.png', 3, 1, NULL, '微辣', 1, 39.00);
INSERT INTO `order_detail` VALUES (4, '钢盆鱿鱼', 'http://47.113.191.68:9000/reggie/钢盆鱿鱼.png', 4, 1, NULL, '微辣', 1, 39.00);
INSERT INTO `order_detail` VALUES (5, '充实单人餐', 'http://47.113.191.68:9000/reggie/单人.png', 5, NULL, 3, NULL, 1, 25.00);
INSERT INTO `order_detail` VALUES (6, '钢盆鱿鱼', 'http://47.113.191.68:9000/reggie/钢盆鱿鱼.png', 6, 1, NULL, '微辣', 1, 39.00);
INSERT INTO `order_detail` VALUES (7, '酸辣巴沙鱼片', 'http://47.113.191.68:9000/reggie/酸辣鱼片.png', 7, 3, NULL, '微辣', 1, 39.00);
INSERT INTO `order_detail` VALUES (8, '酸辣巴沙鱼片', 'http://47.113.191.68:9000/reggie/酸辣鱼片.png', 8, 3, NULL, '微辣', 1, 39.00);
INSERT INTO `order_detail` VALUES (9, '钢盆鱿鱼', 'http://47.113.191.68:9000/reggie/钢盆鱿鱼.png', 9, 1, NULL, '微辣', 1, 39.00);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `number` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '订单号',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '订单状态 1待付款 2待接单 3已接单 4已完成 5已取消 7退款',
  `user_id` bigint(20) NOT NULL COMMENT '下单用户',
  `diningType` tinyint(1) NULL DEFAULT 0 COMMENT '0 堂食 1打包',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `checkout_time` datetime(0) NULL DEFAULT NULL COMMENT '结账时间',
  `pay_method` int(11) NOT NULL DEFAULT 1 COMMENT '支付方式 1微信,2支付宝',
  `pay_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '支付状态 0未支付 1已支付 2退款',
  `amount` decimal(10, 2) NOT NULL COMMENT '实收金额',
  `remark` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '备注',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '手机号',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '用户名称',
  `consignee` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '收货人',
  `cancel_reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '订单取消原因',
  `rejection_reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '订单拒绝原因',
  `cancel_time` datetime(0) NULL DEFAULT NULL COMMENT '订单取消时间',
  `estimated_delivery_time` datetime(0) NULL DEFAULT NULL COMMENT '预计完成时间',
  `delivery_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '配送状态  1立即完成  0选择具体时间',
  `delivery_time` datetime(0) NULL DEFAULT NULL COMMENT '送达时间',
  `pack_amount` int(11) NULL DEFAULT NULL COMMENT '打包费',
  `tableware_number` int(11) NULL DEFAULT NULL COMMENT '餐具数量',
  `tableware_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '餐具数量状态  1按餐量提供  0选择具体数量',
  `address_book_id` bigint(20) NULL DEFAULT NULL COMMENT '地址id',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, '1716202435741', 5, 4, 0, '2024-05-20 18:53:56', NULL, 2, 0, 39.00, '', ' ', '微信用户', NULL, '订单超时，自动取消', NULL, '2024-05-20 19:09:00', '2024-05-20 19:05:00', 0, NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `orders` VALUES (2, '1716202933495', 5, 4, 0, '2024-05-20 19:02:13', NULL, 2, 0, 39.00, '', ' ', '微信用户', NULL, '订单超时，自动取消', NULL, '2024-05-20 19:18:11', '2024-05-20 19:14:00', 0, NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `orders` VALUES (3, '1716203262649', 2, 4, 0, '2024-05-20 19:07:43', '2024-05-20 19:13:49', 2, 1, 39.00, '', ' ', '微信用户', NULL, NULL, NULL, NULL, '2024-05-20 19:19:00', 0, NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `orders` VALUES (4, '1716204016514', 5, 4, 0, '2024-05-20 19:20:17', NULL, 2, 0, 39.00, '', ' ', '微信用户', NULL, '订单超时，自动取消', NULL, '2024-05-20 19:36:00', '2024-05-20 19:32:00', 0, NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `orders` VALUES (5, '1716204268338', 2, 4, 0, '2024-05-20 19:24:28', '2024-05-20 19:25:06', 2, 1, 25.00, '', ' ', '微信用户', NULL, NULL, NULL, NULL, '2024-05-20 19:36:00', 0, NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `orders` VALUES (6, '1717179152256', 5, 4, 0, '2024-06-01 02:12:32', NULL, 2, 0, 39.00, '', ' ', '微信用户', NULL, '订单超时，自动取消', NULL, '2024-06-01 14:09:01', '2024-06-01 10:30:00', 0, NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `orders` VALUES (7, '1717179451718', 2, 4, 0, '2024-06-01 02:17:32', '2024-06-01 02:18:06', 2, 1, 39.00, '', ' ', '微信用户', NULL, NULL, NULL, NULL, '2024-06-01 02:29:00', 0, NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `orders` VALUES (8, '1717222458484', 5, 4, 0, '2024-06-01 14:14:18', NULL, 2, 0, 39.00, '', ' ', '微信用户', NULL, '订单超时，自动取消', NULL, '2024-06-01 15:11:33', '2024-06-01 14:26:00', 0, NULL, 1, 1, 1, NULL, NULL);
INSERT INTO `orders` VALUES (9, '1717222527466', 5, 4, 0, '2024-06-01 14:15:27', NULL, 2, 0, 39.00, '', ' ', '微信用户', NULL, '订单超时，自动取消', NULL, '2024-06-01 15:11:33', '2024-06-01 14:27:00', 0, NULL, 1, 1, 1, NULL, NULL);

-- ----------------------------
-- Table structure for setmeal
-- ----------------------------
DROP TABLE IF EXISTS `setmeal`;
CREATE TABLE `setmeal`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint(20) NOT NULL COMMENT '菜品分类id',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '套餐名称',
  `price` decimal(10, 2) NOT NULL COMMENT '套餐价格',
  `status` int(11) NULL DEFAULT 1 COMMENT '售卖状态 0:停售 1:起售',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '描述信息',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '图片',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_setmeal_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '套餐' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of setmeal
-- ----------------------------
INSERT INTO `setmeal` VALUES (3, 9, '充实单人餐', 25.00, 1, '', 'http://47.113.191.68:9000/reggie/单人.png', '2024-05-06 01:15:27', '2024-05-06 01:21:42', 1, 1);
INSERT INTO `setmeal` VALUES (4, 8, '实惠双人餐', 69.00, 0, '钢盆鱿鱼+酸辣巴沙鱼片+小炒青菜', 'http://47.113.191.68:9000/reggie/双人套餐.png', '2024-05-06 01:21:37', '2024-05-19 23:39:10', 1, 1);

-- ----------------------------
-- Table structure for setmeal_dish
-- ----------------------------
DROP TABLE IF EXISTS `setmeal_dish`;
CREATE TABLE `setmeal_dish`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `setmeal_id` bigint(20) NULL DEFAULT NULL COMMENT '套餐id',
  `dish_id` bigint(20) NULL DEFAULT NULL COMMENT '菜品id',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '菜品名称 （冗余字段）',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '菜品单价（冗余字段）',
  `copies` int(11) NULL DEFAULT NULL COMMENT '菜品份数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '套餐菜品关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of setmeal_dish
-- ----------------------------
INSERT INTO `setmeal_dish` VALUES (1, 3, 11, '热卤鸭锁骨', 9.00, 1);
INSERT INTO `setmeal_dish` VALUES (2, 3, 12, '米饭', 1.50, 1);
INSERT INTO `setmeal_dish` VALUES (3, 3, 2, '辣椒炒肉', 26.00, 1);
INSERT INTO `setmeal_dish` VALUES (4, 4, 12, '米饭', 1.50, 2);
INSERT INTO `setmeal_dish` VALUES (5, 4, 17, '小炒青菜', 9.00, 1);
INSERT INTO `setmeal_dish` VALUES (6, 4, 3, '酸辣巴沙鱼片', 39.00, 1);
INSERT INTO `setmeal_dish` VALUES (7, 4, 1, '钢盆鱿鱼', 39.00, 1);

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '商品名称',
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '图片',
  `user_id` bigint(20) NOT NULL COMMENT '主键',
  `dish_id` bigint(20) NULL DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint(20) NULL DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '口味',
  `number` int(11) NOT NULL DEFAULT 1 COMMENT '数量',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10312 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '购物车' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openid` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '微信用户唯一标识',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '姓名',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '性别',
  `id_number` varchar(18) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '身份证号',
  `avatar` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '用户信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (4, 'oBY6J65F01BU6wUtAdnJdQ1O9nV0', '微信用户', ' ', NULL, NULL, NULL, '2023-08-26 11:18:33');
INSERT INTO `user` VALUES (5, 'oBY6J6-bmqK8-K1ru7CmV7I-pbEg', '微信用户', NULL, NULL, NULL, NULL, '2024-05-06 01:38:32');

SET FOREIGN_KEY_CHECKS = 1;
