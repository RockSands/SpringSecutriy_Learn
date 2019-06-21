DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT '' COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '登录密码',
  `name` varchar(255) DEFAULT NULL COMMENT '用户真实姓名',
  `id_card_num` varchar(255) DEFAULT NULL COMMENT '用户身份证号',
  `state` char(1) DEFAULT '0' COMMENT '用户状态：0:正常状态,1：用户被锁定',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `username` (`username`) USING BTREE,
  UNIQUE KEY `id_card_num` (`id_card_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `available` char(1) DEFAULT '0' COMMENT '是否可用0可用  1不可用',
  `role` varchar(20) DEFAULT NULL COMMENT '角色标识程序中判断使用,如"admin"',
  `description` varchar(100) DEFAULT NULL COMMENT '角色描述,UI界面显示使用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role` (`role`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `uid` int(11) DEFAULT NULL COMMENT '用户id',
  `role_id` int(11) DEFAULT NULL COMMENT '角色id',
  KEY `uid` (`uid`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` int(11) DEFAULT NULL COMMENT '父编号,本权限可能是该父编号权限的子权限',
  `parent_ids` varchar(20) DEFAULT NULL COMMENT '父编号列表',
  `permission` varchar(100) DEFAULT NULL COMMENT '权限字符串,menu例子：role:*，button例子：role:create,role:update,role:delete,role:view',
  `resource_type` varchar(20) DEFAULT NULL COMMENT '资源类型，[menu|button]',
  `url` varchar(200) DEFAULT NULL COMMENT '资源路径 如：/userinfo/list',
  `name` varchar(50) DEFAULT NULL COMMENT '权限名称',
  `available` char(1) DEFAULT '0' COMMENT '是否可用0可用  1不可用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `role_id` int(11) DEFAULT NULL COMMENT '角色id',
  `permission_id` int(11) DEFAULT NULL COMMENT '权限id',
  KEY `role_id` (`role_id`) USING BTREE,
  KEY `permission_id` (`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#插入用户信息表
INSERT INTO user_info(uid,username,`password`,`name`,id_card_num) VALUES (null,'admin','123456','超哥','133333333333333333');
INSERT INTO user_info(uid,username,`password`,`name`,id_card_num) VALUES (null,'test','123456','孙悟空','155555555555555555');
#插入用户角色表
INSERT INTO `role` (`id`,`available`,`description`,`role`) VALUES (null,0,'管理员','admin');
INSERT INTO `role` (`id`,`available`,`description`,`role`) VALUES (null,0,'VIP会员','vip');
INSERT INTO `role` (`id`,`available`,`description`,`role`) VALUES (null,1,'测试','test');
#插入用户_角色关联表
INSERT INTO `user_role` (`role_id`,`uid`) VALUES (1,1);
INSERT INTO `user_role` (`role_id`,`uid`) VALUES (2,2);
#插入权限表
INSERT INTO `permission` (`id`,`available`,`name`,`parent_id`,`parent_ids`,`permission`,`resource_type`,`url`) VALUES (null,0,'用户管理',0,'0/','userInfo:view','menu','userInfo/view');
INSERT INTO `permission` (`id`,`available`,`name`,`parent_id`,`parent_ids`,`permission`,`resource_type`,`url`) VALUES (null,0,'用户添加',1,'0/1','userInfo:add','button','userInfo/add');
INSERT INTO `permission` (`id`,`available`,`name`,`parent_id`,`parent_ids`,`permission`,`resource_type`,`url`) VALUES (null,0,'用户删除',1,'0/1','userInfo:del','button','userInfo/del');
#插入角色_权限表
INSERT INTO `role_permission` (`permission_id`,`role_id`) VALUES (1,1);
INSERT INTO `role_permission` (`permission_id`,`role_id`) VALUES (2,1);
INSERT INTO `role_permission` (`permission_id`,`role_id`) VALUES (3,2);
