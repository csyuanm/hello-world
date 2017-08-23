/*
SQLyog Ultimate v11.27 (32 bit)
MySQL - 5.6.33 : Database - mml
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mml` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `mml`;

/*Table structure for table `ding_process_receive` */

DROP TABLE IF EXISTS `ding_process_receive`;

CREATE TABLE `ding_process_receive` (
  `approver_userid_list` varchar(64) DEFAULT NULL COMMENT '审批人列表',
  `create_time` date DEFAULT NULL COMMENT '创建时间',
  `finish_time` date DEFAULT NULL COMMENT '完成时间',
  `form_component_values` varchar(64) DEFAULT NULL COMMENT '审批的内容',
  `originator_dept_id` varchar(64) DEFAULT NULL COMMENT '发起人所在部门',
  `originator_userid` varchar(64) DEFAULT NULL COMMENT '发起人id',
  `process_instance_id` varchar(64) DEFAULT NULL COMMENT '审批实例id',
  `process_instance_result` varchar(64) DEFAULT NULL COMMENT '审批结果',
  `status` varchar(16) DEFAULT NULL COMMENT '审批状态',
  `title` varchar(32) DEFAULT NULL COMMENT '审批类型'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `netsuite_purchase_approve` */

DROP TABLE IF EXISTS `netsuite_purchase_approve`;

CREATE TABLE `netsuite_purchase_approve` (
  `entity` varchar(32) DEFAULT NULL COMMENT 'Click New to set up a new vendor',
  `trandate` date DEFAULT NULL COMMENT '交易日期',
  `duedate` date DEFAULT NULL COMMENT '截止日期',
  `createdby` varchar(16) DEFAULT NULL COMMENT '申请人',
  `custbodyapprover` varchar(64) DEFAULT NULL COMMENT '审批人列表',
  `tranid` varchar(32) DEFAULT NULL COMMENT '订单编号',
  `amount` float DEFAULT NULL COMMENT '订单金额',
  `location` varchar(32) DEFAULT NULL COMMENT '接受门店'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
