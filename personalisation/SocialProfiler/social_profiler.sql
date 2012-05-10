-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.33-community


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema social
--

CREATE DATABASE IF NOT EXISTS social;
USE social;

--
-- Definition of table `blog`
--

DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog` (
  `week` int(11) NOT NULL,
  PRIMARY KEY (`week`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `blog_info`
--

DROP TABLE IF EXISTS `blog_info`;
CREATE TABLE `blog_info` (
  `week` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`week`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `info`
--

DROP TABLE IF EXISTS `info`;
CREATE TABLE `info` (
  `week` int(11) NOT NULL,
  `starts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `profile` tinyint(4) NOT NULL,
  `last_time` bigint(20) NOT NULL,
  `last_time_timestamp` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `number_actions` smallint(6) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`week`,`profile`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `info_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`facebook_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Definition of table `total_info`
--

DROP TABLE IF EXISTS `total_info`;
CREATE TABLE `total_info` (
  `week` int(11) NOT NULL,
  `profile` tinyint(4) NOT NULL,
  `number_actions` smallint(6) NOT NULL,
  PRIMARY KEY (`week`,`profile`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Definition of table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `facebook_id` bigint(20) NOT NULL,
  `ca_id` varchar(35) NOT NULL,
  PRIMARY KEY (`facebook_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
