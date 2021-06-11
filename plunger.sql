/*
Navicat MySQL Data Transfer

Source Server         : localhost3306
Source Server Version : 50731
Source Host           : localhost:3306
Source Database       : plunger

Target Server Type    : MYSQL
Target Server Version : 50731
File Encoding         : 65001

Date: 2021-06-11 11:59:14
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
  `memo` longtext,
  PRIMARY KEY (`unid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dict
-- ----------------------------
INSERT INTO `dict` VALUES ('026ff0a30132483e9d67a25c5702d8e2', 'system', 'resultPath', 'result/', '结果文件保存路径');
INSERT INTO `dict` VALUES ('36f0f16df2af4fedb18f9b80d7688504', 'excel-shuizhun', 'sheetNames', '开挖水准(2)', '水准>8时需要打印的sheet页名称');
INSERT INTO `dict` VALUES ('382f63c90c0d43729189ff036f5035dc', 'excel-wujin', 'sheetNames', '井基砼隐,井基砼隐 (2)', '无井>0时需要打印的sheet页名称');
INSERT INTO `dict` VALUES ('3c7883d55e1a44a5a00200f503471f3a', 'excel-basic', 'c30CellAddr', 'GL11', '判断C30的单元格地址');
INSERT INTO `dict` VALUES ('50c5a8bbee004cb18063d9f016d596a3', 'excel-wutiaojian', 'sheetNames', '挖隐,挖隐 (2)', '无条件需要打印的sheet页');
INSERT INTO `dict` VALUES ('56ab6564c415462fbee7876bca8f8eba', 'excel-c15', 'sheetNames', '浇筑记录C15,旁站记录C15', 'C15需要打印的sheet页名称');
INSERT INTO `dict` VALUES ('596cab62785f4b329153f03bcd72984c', 'excel-basic', 'wujinCellAddr', 'GF9', '判断无井是否>0的单元格地址');
INSERT INTO `dict` VALUES ('71e3276467784120b4f256107f458198', 'excel-basic', 'baoguanCellAddr', 'GD12', '判断包管的单元格地址');
INSERT INTO `dict` VALUES ('74ea2584eef34d588c215ce39aaa1b8b', 'excel-basic', 'shuizhunCellAddr', 'GA16', '判断水准的单元格地址');
INSERT INTO `dict` VALUES ('86e2a666caf147c181d340408a3777d9', 'system', 'uploadPath', 'upload/', '上传文件保存路径');
INSERT INTO `dict` VALUES ('92544d4f96054831980a5f0610b653d3', 'excel-baoguan', 'sheetNames', '包筋隐,包筋隐 (2)', '包管需要打印的sheet页名称');
INSERT INTO `dict` VALUES ('a41fa2c170c14528b5dbc542b8e1a26f', 'excel-basic', 'c15CellAddr', 'GL8', '判断C15的单元格地址');
INSERT INTO `dict` VALUES ('adc64f0bec7a4093b9df0d66593c5f5b', 'excel-c30', 'sheetNames', '浇筑记录C30,旁站记录C30', 'C30需要打印的sheet页名称');
INSERT INTO `dict` VALUES ('b21ff937cd74484091c84363a940170c', 'excel-fang', 'sheetNames', '井石垫隐,井石垫隐 (2)', '方>0时需要打印的sheet页名称');
INSERT INTO `dict` VALUES ('cbacaee18edb41bab4f661c6d757dc4c', 'excel-basic', 'fangCellAddr', 'GE9', '判断方是否>0的单元格地址');
INSERT INTO `dict` VALUES ('dbdb636fb589409696580bfeaca59a86', 'excel-data', 'sheetName', '统计资料', '数据源sheet页');
INSERT INTO `dict` VALUES ('deb8aeeb1942431b83eadd35b3797d94', 'excel-basic', 'printCellAddr', 'FW9', '打印页码变更单元格地址');
INSERT INTO `dict` VALUES ('e0e59003b9d347dd8ef8f8325cf7e435', 'excel-basic', 'yuanCellAddr', 'GD9', '判断圆是否>0的单元格地址');
INSERT INTO `dict` VALUES ('f9206a6ee14d4289a63019654e7e1b0b', 'excel-yuan', 'sheetNames', '井素砼垫隐,井素砼垫隐 (2),井基筋安隐,井基筋安隐 (2)', '圆>0时需要打印的sheet页名称');
INSERT INTO `dict` VALUES ('fbddc55bd7f04ef0a6a6d199bd4ee955', 'excel-basic', 'sheetName', '挖隐', '页码变更的sheet页名称');
