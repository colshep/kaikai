/*
Navicat MySQL Data Transfer

Source Server         : localhost3306
Source Server Version : 50731
Source Host           : localhost:3306
Source Database       : plunger

Target Server Type    : MYSQL
Target Server Version : 50731
File Encoding         : 65001

Date: 2021-06-04 16:45:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dict
-- ----------------------------
DROP TABLE IF EXISTS `dict`;
CREATE TABLE `dict` (
  `unid` varchar(32) NOT NULL,
  `type` varchar(64) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `value` longtext,
  PRIMARY KEY (`unid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dict
-- ----------------------------
INSERT INTO `dict` VALUES ('026ff0a30132483e9d67a25c5702d8e2', 'system', 'resultPath', 'result/');
INSERT INTO `dict` VALUES ('382f63c90c0d43729189ff036f5035dc', 'excel-jin', 'sheetNames', '井基砼隐,井基砼隐 (2)');
INSERT INTO `dict` VALUES ('596cab62785f4b329153f03bcd72984c', 'excel-basic', 'jinCellAddr', 'GF9');
INSERT INTO `dict` VALUES ('86e2a666caf147c181d340408a3777d9', 'system', 'uploadPath', 'upload/');
INSERT INTO `dict` VALUES ('b21ff937cd74484091c84363a940170c', 'excel-fang', 'sheetNames', '井石垫隐,井石垫隐 (2)');
INSERT INTO `dict` VALUES ('cbacaee18edb41bab4f661c6d757dc4c', 'excel-basic', 'fangCellAddr', 'GE9');
INSERT INTO `dict` VALUES ('dbdb636fb589409696580bfeaca59a86', 'excel-data', 'sheetName', '统计资料');
INSERT INTO `dict` VALUES ('deb8aeeb1942431b83eadd35b3797d94', 'excel-basic', 'printCellAddr', 'FW9');
INSERT INTO `dict` VALUES ('e0e59003b9d347dd8ef8f8325cf7e435', 'excel-basic', 'yuanCellAddr', 'GD9');
INSERT INTO `dict` VALUES ('f9206a6ee14d4289a63019654e7e1b0b', 'excel-yuan', 'sheetNames', '井素砼垫隐,井素砼垫隐 (2),井基筋安隐,井基筋安隐 (2)');
INSERT INTO `dict` VALUES ('fbddc55bd7f04ef0a6a6d199bd4ee955', 'excel-basic', 'sheetName', '挖隐');
