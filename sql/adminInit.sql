/** 권재은 mysql에서 추출한 sql데이터
<admin 계정>
- 아이디: admin01
- 비밀번호: 123456789
  */

-- MySQL dump 10.13  Distrib 8.0.41, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: goorm_exp
-- ------------------------------------------------------
-- Server version	8.4.4

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `attendance_date` date NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6y2t3r1ig8a9ccqwbrs6wk4l8` (`user_id`,`attendance_date`),
  CONSTRAINT `FKjcaqd29v2qy723owsdah2t8vx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (1,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-02-21',1),(2,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-02-21',2),(3,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-02-21',3),(4,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-03-21',1),(5,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-03-21',4),(6,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-03-21',5),(7,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-04-21',2),(8,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-04-21',6),(9,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-04-21',7),(10,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-05-21',1),(11,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-05-21',3),(12,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-05-21',8),(13,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-01-19',9),(14,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-01-19',10),(15,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-07-21',2),(16,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-07-21',11),(17,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-07-19',5),(18,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-07-19',12),(19,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-07-20',13),(20,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','2025-07-20',14);
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmark`
--

DROP TABLE IF EXISTS `bookmark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmark` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `link` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo4vbqvq5trl11d85bqu5kl870` (`user_id`),
  CONSTRAINT `FKo4vbqvq5trl11d85bqu5kl870` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmark`
--

LOCK TABLES `bookmark` WRITE;
/*!40000 ALTER TABLE `bookmark` DISABLE KEYS */;
INSERT INTO `bookmark` VALUES (1,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://spring.io/projects/spring-boot','Spring Boot 공식 문서',1),(2,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://react.dev/','React 공식 가이드',2),(3,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://docs.oracle.com/javase/tutorial/','Java 튜토리얼',3),(4,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://developer.mozilla.org/','MDN Web Docs',4),(5,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://www.acmicpc.net/','백준 온라인 저지',5),(6,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://github.com/','GitHub',6),(7,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://stackoverflow.com/','Stack Overflow',7),(8,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://leetcode.com/','LeetCode',8),(9,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://school.programmers.co.kr/','Programmers',9),(10,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://vuejs.org/','Vue.js 공식 문서',10),(11,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://docs.python.org/','Python 공식 문서',11),(12,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://www.w3schools.com/','W3Schools',12),(13,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://nodejs.org/','Node.js 공식 사이트',13),(14,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://codepen.io/','CodePen',14),(15,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://dev.mysql.com/doc/','MySQL 공식 문서',15),(16,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://docs.docker.com/','Docker 공식 문서',16),(17,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://docs.aws.amazon.com/','AWS 문서',17),(18,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://www.inflearn.com/','인프런',18),(19,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://www.youtube.com/user/egoing2','YouTube 생활코딩',19),(20,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','https://www.codecademy.com/','Codecademy',20);
/*!40000 ALTER TABLE `bookmark` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_emoji_reaction`
--

DROP TABLE IF EXISTS `comment_emoji_reaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_emoji_reaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `emoji` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `comment_id` bigint NOT NULL,
  `reaction_writer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2mkii6k9q0d0vlw5tpuagthtn` (`comment_id`),
  KEY `FKmdnm17oown7m1xe1bbfaum0qh` (`reaction_writer_id`),
  CONSTRAINT `FK2mkii6k9q0d0vlw5tpuagthtn` FOREIGN KEY (`comment_id`) REFERENCES `praise_comment` (`id`),
  CONSTRAINT `FKmdnm17oown7m1xe1bbfaum0qh` FOREIGN KEY (`reaction_writer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_emoji_reaction`
--

LOCK TABLES `comment_emoji_reaction` WRITE;
/*!40000 ALTER TABLE `comment_emoji_reaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_emoji_reaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mission`
--

DROP TABLE IF EXISTS `mission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `description` varchar(1023) COLLATE utf8mb4_general_ci NOT NULL,
  `difficulty` enum('EASY','HARD','NORMAL') COLLATE utf8mb4_general_ci NOT NULL,
  `mission_line` int NOT NULL,
  `name` varchar(127) COLLATE utf8mb4_general_ci NOT NULL,
  `mission_order` int NOT NULL,
  `reward_exp` int NOT NULL,
  `reward_point` int NOT NULL,
  `tip` varchar(511) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mission_group_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3jub8gblvkdf7uxmk8wdew113` (`mission_group_id`),
  CONSTRAINT `FK3jub8gblvkdf7uxmk8wdew113` FOREIGN KEY (`mission_group_id`) REFERENCES `mission_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mission`
--

LOCK TABLES `mission` WRITE;
/*!40000 ALTER TABLE `mission` DISABLE KEYS */;
/*!40000 ALTER TABLE `mission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mission_group`
--

DROP TABLE IF EXISTS `mission_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mission_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `name` varchar(127) COLLATE utf8mb4_general_ci NOT NULL,
  `teacher_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl8g4ecvtsbb97eg8r6qgw7vwh` (`teacher_id`),
  CONSTRAINT `FKl8g4ecvtsbb97eg8r6qgw7vwh` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mission_group`
--

LOCK TABLES `mission_group` WRITE;
/*!40000 ALTER TABLE `mission_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `mission_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mission_reward_exp_log`
--

DROP TABLE IF EXISTS `mission_reward_exp_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mission_reward_exp_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `note` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `next_exp` int DEFAULT NULL,
  `prev_exp` int DEFAULT NULL,
  `user_mission_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1imsw9sfotw9df43jkavs4c3v` (`user_mission_id`),
  CONSTRAINT `FK1imsw9sfotw9df43jkavs4c3v` FOREIGN KEY (`user_mission_id`) REFERENCES `user_mission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mission_reward_exp_log`
--

LOCK TABLES `mission_reward_exp_log` WRITE;
/*!40000 ALTER TABLE `mission_reward_exp_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `mission_reward_exp_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mission_reward_point_log`
--

DROP TABLE IF EXISTS `mission_reward_point_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mission_reward_point_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `note` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `next_point` int DEFAULT NULL,
  `prev_point` int DEFAULT NULL,
  `user_mission_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7k9ed2500etdhj79x0rfq4v7p` (`user_mission_id`),
  CONSTRAINT `FK7k9ed2500etdhj79x0rfq4v7p` FOREIGN KEY (`user_mission_id`) REFERENCES `user_mission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mission_reward_point_log`
--

LOCK TABLES `mission_reward_point_log` WRITE;
/*!40000 ALTER TABLE `mission_reward_point_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `mission_reward_point_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mission_task`
--

DROP TABLE IF EXISTS `mission_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mission_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `name` varchar(127) COLLATE utf8mb4_general_ci NOT NULL,
  `score` int NOT NULL,
  `tip` varchar(511) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mission_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc1td84porlqtgl28xlwao261i` (`mission_id`),
  CONSTRAINT `FKc1td84porlqtgl28xlwao261i` FOREIGN KEY (`mission_id`) REFERENCES `mission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mission_task`
--

LOCK TABLES `mission_task` WRITE;
/*!40000 ALTER TABLE `mission_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `mission_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `content` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `is_read` bit(1) NOT NULL,
  `target_id` bigint NOT NULL,
  `type` enum('DIARY_COMMENT','DIARY_LIKE','LEVEL_UP','PRAISE_RECEIVED') COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnk4ftb5am9ubmkv1661h15ds9` (`user_id`),
  CONSTRAINT `FKnk4ftb5am9ubmkv1661h15ds9` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `receiver_phone_number` varchar(13) COLLATE utf8mb4_general_ci NOT NULL,
  `product_id` bigint NOT NULL,
  `purchaser_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK787ibr3guwp6xobrpbofnv7le` (`product_id`),
  KEY `FKa45uf1mpbvwuufmj47d0cwnej` (`purchaser_id`),
  CONSTRAINT `FK787ibr3guwp6xobrpbofnv7le` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKa45uf1mpbvwuufmj47d0cwnej` FOREIGN KEY (`purchaser_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-3570-0248',1,20),(2,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-0246-7915',1,17),(3,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-7913-4682',1,14),(4,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-4680-1379',1,11),(5,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-1357-2468',1,8),(6,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-8901-2345',1,5),(7,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-5678-9012',1,2),(8,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-2469-9137',2,19),(9,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-9135-6804',2,16),(10,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-6802-3571',2,13),(11,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-3579-2460',2,10),(12,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-0123-4567',2,7),(13,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-7890-1234',2,4),(14,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-4567-8901',2,1),(15,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-1358-8026',3,18),(16,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-8024-5793',3,15),(17,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-5791-2468',3,12),(18,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-2468-1357',3,9),(19,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-9012-3456',3,6),(20,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','010-6789-0123',3,3);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `praise`
--

DROP TABLE IF EXISTS `praise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `praise` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `content` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `praise_type` enum('CHEER','RECOGNIZE','THANKS') COLLATE utf8mb4_general_ci NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2cgijvqcvx3lb6xb84q64efvc` (`sender_id`),
  CONSTRAINT `FK2cgijvqcvx3lb6xb84q64efvc` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `praise`
--

LOCK TABLES `praise` WRITE;
/*!40000 ALTER TABLE `praise` DISABLE KEYS */;
INSERT INTO `praise` VALUES (1,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘 알고리즘 문제 해결하는 걸 보니 정말 대단해요!','THANKS',1),(2,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','프로젝트에서 많은 도움을 주셔서 감사합니다.','CHEER',2),(3,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 기술 도전하는 모습이 멋져요! 파이팅!','RECOGNIZE',3),(4,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코드 리뷰를 정말 꼼꼼히 해주셔서 감사해요.','THANKS',4),(5,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','버그 수정하느라 고생하셨어요. 정말 대단해요!','CHEER',5),(6,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','늦은 시간까지 개발하시는 모습 응원해요!','RECOGNIZE',6),(7,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','깔끔한 코드 작성 실력이 인정됩니다!','THANKS',7),(8,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','팀 프로젝트에서 많은 기여를 해주셔서 감사해요.','CHEER',8),(9,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 프레임워크 학습하시는 열정이 대단해요!','RECOGNIZE',9),(10,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터베이스 설계를 잘 해주셔서 감사합니다.','THANKS',10),(11,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','API 문서 정리해주신 거 정말 도움이 됐어요!','CHEER',11),(12,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','힘든 시기에도 포기하지 않고 계속 노력하세요!','RECOGNIZE',12),(13,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Git 사용법 알려주셔서 정말 감사해요.','THANKS',13),(14,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','테스트 코드 작성 실력이 정말 좋아요!','CHEER',14),(15,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 도전 응원합니다! 화이팅!','RECOGNIZE',15),(16,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','배포 과정에서 많은 도움을 주셔서 감사해요.','THANKS',16),(17,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','복잡한 로직을 깔끔하게 정리하는 능력이 대단해요!','CHEER',17),(18,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','스터디 그룹 운영해주셔서 감사합니다.','RECOGNIZE',18),(19,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','어려운 개념도 쉽게 설명해주시는 능력이 인정돼요!','THANKS',19),(20,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','포기하지 말고 끝까지 해내세요! 응원해요!','CHEER',20),(21,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코드 최적화 작업 고생하셨어요!','RECOGNIZE',21),(22,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 기술 스택 도입하는 모습이 멋져요!','THANKS',22),(23,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','밤늦게까지 개발하시느라 고생 많으세요. 화이팅!','CHEER',23),(24,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오류 해결해주셔서 정말 감사해요.','RECOGNIZE',24),(25,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','창의적인 해결책을 제시하는 능력이 대단해요!','THANKS',25),(26,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 언어 배우시는 열정 응원해요!','CHEER',26),(27,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','프론트엔드 작업 도움 주셔서 감사합니다.','RECOGNIZE',27),(28,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','백엔드 아키텍처 설계 실력이 인정돼요!','THANKS',28),(29,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','힘들어도 포기하지 마세요! 꼭 해내실 거예요!','CHEER',29),(30,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','퍼포먼스 튜닝 작업 감사해요.','RECOGNIZE',30),(31,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','보안 이슈 해결해주셔서 정말 감사합니다!','THANKS',31),(32,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','클린 코드 작성 스타일이 정말 좋아요!','CHEER',32),(33,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 프로젝트 시작하는 것 응원해요!','RECOGNIZE',33),(34,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CI/CD 파이프라인 구축해주셔서 감사해요.','THANKS',34),(35,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','문제 해결 능력이 정말 뛰어나세요!','CHEER',35),(36,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코딩 테스트 준비하는 모습 응원합니다!','RECOGNIZE',36),(37,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','좋은 라이브러리 추천해주셔서 감사해요.','THANKS',37),(38,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','리팩토링 실력이 정말 인정됩니다!','CHEER',38),(39,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','취업 준비하시는 것 화이팅해요!','RECOGNIZE',39),(40,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코드 리뷰에서 좋은 피드백 주셔서 감사해요.','THANKS',40),(41,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','알고리즘 최적화 실력이 대단해요!','CHEER',41),(42,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 도전 계속 응원할게요!','RECOGNIZE',42),(43,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','버전 관리 잘 해주셔서 감사합니다.','THANKS',43),(44,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','디자인 패턴 적용 능력이 뛰어나세요!','CHEER',44),(45,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','개발자 스터디 모임 열심히 하세요!','RECOGNIZE',45),(46,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','예외 처리 잘 해주셔서 감사해요.','THANKS',46),(47,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코드 가독성이 정말 좋아요!','CHEER',47),(48,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 기술 도전하는 용기 응원해요!','RECOGNIZE',48),(49,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터 분석 작업 도움 주셔서 감사해요.','THANKS',49),(50,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','문서화 능력이 정말 훌륭해요!','CHEER',50),(51,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘 알고리즘 문제 해결하는 걸 보니 정말 대단해요!','RECOGNIZE',1),(52,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','프로젝트에서 많은 도움을 주셔서 감사합니다.','THANKS',2),(53,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 기술 도전하는 모습이 멋져요! 파이팅!','CHEER',3),(54,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코드 리뷰를 정말 꼼꼼히 해주셔서 감사해요.','RECOGNIZE',4),(55,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','버그 수정하느라 고생하셨어요. 정말 대단해요!','THANKS',5),(56,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','늦은 시간까지 개발하시는 모습 응원해요!','CHEER',6),(57,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','깔끔한 코드 작성 실력이 인정됩니다!','RECOGNIZE',7),(58,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','팀 프로젝트에서 많은 기여를 해주셔서 감사해요.','THANKS',8),(59,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 프레임워크 학습하시는 열정이 대단해요!','CHEER',9),(60,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터베이스 설계를 잘 해주셔서 감사합니다.','RECOGNIZE',10),(61,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','API 문서 정리해주신 거 정말 도움이 됐어요!','THANKS',11),(62,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','힘든 시기에도 포기하지 않고 계속 노력하세요!','CHEER',12),(63,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Git 사용법 알려주셔서 정말 감사해요.','RECOGNIZE',13),(64,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','테스트 코드 작성 실력이 정말 좋아요!','THANKS',14),(65,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 도전 응원합니다! 화이팅!','CHEER',15),(66,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','배포 과정에서 많은 도움을 주셔서 감사해요.','RECOGNIZE',16),(67,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','복잡한 로직을 깔끔하게 정리하는 능력이 대단해요!','THANKS',17),(68,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','스터디 그룹 운영해주셔서 감사합니다.','CHEER',18),(69,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','어려운 개념도 쉽게 설명해주시는 능력이 인정돼요!','RECOGNIZE',19),(70,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','포기하지 말고 끝까지 해내세요! 응원해요!','THANKS',20),(71,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코드 최적화 작업 고생하셨어요!','CHEER',21),(72,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 기술 스택 도입하는 모습이 멋져요!','RECOGNIZE',22),(73,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','밤늦게까지 개발하시느라 고생 많으세요. 화이팅!','THANKS',23),(74,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오류 해결해주셔서 정말 감사해요.','CHEER',24),(75,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','창의적인 해결책을 제시하는 능력이 대단해요!','RECOGNIZE',25),(76,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 언어 배우시는 열정 응원해요!','THANKS',26),(77,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','프론트엔드 작업 도움 주셔서 감사합니다.','CHEER',27),(78,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','백엔드 아키텍처 설계 실력이 인정돼요!','RECOGNIZE',28),(79,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','힘들어도 포기하지 마세요! 꼭 해내실 거예요!','THANKS',29),(80,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','퍼포먼스 튜닝 작업 감사해요.','CHEER',30),(81,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','보안 이슈 해결해주셔서 정말 감사합니다!','RECOGNIZE',31),(82,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','클린 코드 작성 스타일이 정말 좋아요!','THANKS',32),(83,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 프로젝트 시작하는 것 응원해요!','CHEER',33),(84,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CI/CD 파이프라인 구축해주셔서 감사해요.','RECOGNIZE',34),(85,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','문제 해결 능력이 정말 뛰어나세요!','THANKS',35),(86,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코딩 테스트 준비하는 모습 응원합니다!','CHEER',36),(87,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','좋은 라이브러리 추천해주셔서 감사해요.','RECOGNIZE',37),(88,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','리팩토링 실력이 정말 인정됩니다!','THANKS',38),(89,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','취업 준비하시는 것 화이팅해요!','CHEER',39),(90,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코드 리뷰에서 좋은 피드백 주셔서 감사해요.','RECOGNIZE',40),(91,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','알고리즘 최적화 실력이 대단해요!','THANKS',41),(92,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 도전 계속 응원할게요!','CHEER',42),(93,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','버전 관리 잘 해주셔서 감사합니다.','RECOGNIZE',43),(94,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','디자인 패턴 적용 능력이 뛰어나세요!','THANKS',44),(95,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','개발자 스터디 모임 열심히 하세요!','CHEER',45),(96,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','예외 처리 잘 해주셔서 감사해요.','RECOGNIZE',46),(97,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코드 가독성이 정말 좋아요!','THANKS',47),(98,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','새로운 기술 도전하는 용기 응원해요!','CHEER',48),(99,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터 분석 작업 도움 주셔서 감사해요.','RECOGNIZE',49),(100,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','문서화 능력이 정말 훌륭해요!','THANKS',50);
/*!40000 ALTER TABLE `praise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `praise_comment`
--

DROP TABLE IF EXISTS `praise_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `praise_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `content` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `comment_writer_id` bigint NOT NULL,
  `praise_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcnpgp4jo9s0768grx41mbod8r` (`comment_writer_id`),
  KEY `FK18sqnm7il17a0iayahjymj3tq` (`praise_id`),
  CONSTRAINT `FK18sqnm7il17a0iayahjymj3tq` FOREIGN KEY (`praise_id`) REFERENCES `praise` (`id`),
  CONSTRAINT `FKcnpgp4jo9s0768grx41mbod8r` FOREIGN KEY (`comment_writer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `praise_comment`
--

LOCK TABLES `praise_comment` WRITE;
/*!40000 ALTER TABLE `praise_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `praise_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `praise_emoji_reaction`
--

DROP TABLE IF EXISTS `praise_emoji_reaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `praise_emoji_reaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `emoji` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `praise_id` bigint NOT NULL,
  `reaction_writer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjqbw29of1pbt29rykb181i4al` (`praise_id`),
  KEY `FKo6jbaqk97jxkl71u0fnusc5gm` (`reaction_writer_id`),
  CONSTRAINT `FKjqbw29of1pbt29rykb181i4al` FOREIGN KEY (`praise_id`) REFERENCES `praise` (`id`),
  CONSTRAINT `FKo6jbaqk97jxkl71u0fnusc5gm` FOREIGN KEY (`reaction_writer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `praise_emoji_reaction`
--

LOCK TABLES `praise_emoji_reaction` WRITE;
/*!40000 ALTER TABLE `praise_emoji_reaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `praise_emoji_reaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `praise_receiver`
--

DROP TABLE IF EXISTS `praise_receiver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `praise_receiver` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `praise_id` bigint DEFAULT NULL,
  `receiver_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp41r7xp6x9llhiblwe9uxs5n4` (`praise_id`),
  KEY `FKdw5ccic6q10qji7yweq1sd5gt` (`receiver_id`),
  CONSTRAINT `FKdw5ccic6q10qji7yweq1sd5gt` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKp41r7xp6x9llhiblwe9uxs5n4` FOREIGN KEY (`praise_id`) REFERENCES `praise` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `praise_receiver`
--

LOCK TABLES `praise_receiver` WRITE;
/*!40000 ALTER TABLE `praise_receiver` DISABLE KEYS */;
INSERT INTO `praise_receiver` VALUES (1,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',1,1),(2,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',2,2),(3,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',3,3),(4,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',4,4),(5,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',5,5),(6,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',6,6),(7,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',7,7),(8,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',8,8),(9,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',9,9),(10,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',10,10),(11,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',11,11),(12,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',12,12),(13,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',13,13),(14,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',14,14),(15,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',15,15),(16,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',16,16),(17,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',17,17),(18,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',18,18),(19,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',19,19),(20,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',20,20),(21,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',21,21),(22,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',22,22),(23,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',23,23),(24,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',24,24),(25,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',25,25),(26,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',26,26),(27,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',27,27),(28,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',28,28),(29,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',29,29),(30,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',30,30),(31,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',31,31),(32,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',32,32),(33,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',33,33),(34,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',34,34),(35,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',35,35),(36,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',36,36),(37,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',37,37),(38,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',38,38),(39,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',39,39),(40,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',40,40),(41,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',41,41),(42,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',42,42),(43,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',43,43),(44,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',44,44),(45,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',45,45),(46,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',46,46),(47,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',47,47),(48,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',48,48),(49,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',49,49),(50,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',50,50),(51,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',51,1),(52,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',52,2),(53,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',53,3),(54,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',54,4),(55,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',55,5),(56,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',56,6),(57,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',57,7),(58,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',58,8),(59,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',59,9),(60,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',60,10),(61,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',61,11),(62,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',62,12),(63,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',63,13),(64,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',64,14),(65,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',65,15),(66,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',66,16),(67,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',67,17),(68,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',68,18),(69,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',69,19),(70,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',70,20),(71,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',71,21),(72,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',72,22),(73,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',73,23),(74,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',74,24),(75,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',75,25),(76,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',76,26),(77,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',77,27),(78,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',78,28),(79,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',79,29),(80,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',80,30),(81,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',81,31),(82,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',82,32),(83,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',83,33),(84,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',84,34),(85,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',85,35),(86,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',86,36),(87,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',87,37),(88,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',88,38),(89,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',89,39),(90,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',90,40),(91,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',91,41),(92,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',92,42),(93,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',93,43),(94,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',94,44),(95,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',95,45),(96,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',96,46),(97,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',97,47),(98,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',98,48),(99,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',99,49),(100,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',100,50);
/*!40000 ALTER TABLE `praise_receiver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `brand` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `is_deleted` bit(1) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `price` int NOT NULL,
  `quantity` int NOT NULL,
  `product_image_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKc5fhf0trey8g74e2jutoy5idn` (`product_image_id`),
  CONSTRAINT `FKef6locfy4ip16tdnk20vkvksi` FOREIGN KEY (`product_image_id`) REFERENCES `product_image` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'오리온',_binary '\0','초코파이 12개입',4500,50,NULL),(2,'농심',_binary '\0','바나나킥 75g',1800,30,NULL),(3,'농심',_binary '\0','컵라면 대용량',2200,25,NULL),(4,'스타벅스',_binary '\0','아메리카노 원두 200g',12000,15,NULL),(5,'해태',_binary '\0','허니버터칩 60g',2500,40,NULL),(6,'서울우유',_binary '\0','딸기우유 200ml',1200,60,NULL),(7,'농심',_binary '\0','새우깡 90g',1900,35,NULL),(8,'김밥천국',_binary '\0','김밥천국 도시락',8500,20,NULL),(9,'빙그레',_binary '\0','초코우유 500ml',2000,45,NULL),(10,'CJ',_binary '\0','치킨너겟 냉동',7800,12,NULL),(11,'오뚜기',_binary '\0','피자 냉동식품',15000,8,NULL),(12,'오뚜기',_binary '\0','라면 5개입',4200,30,NULL),(13,'롯데',_binary '\0','아이스크림 6개입',9500,18,NULL),(14,'파리바게뜨',_binary '\0','샌드위치',5500,22,NULL),(15,'해태',_binary '\0','과자 선물세트',25000,10,NULL),(16,'레드불',_binary '\0','에너지드링크 250ml',3000,50,NULL),(17,'CJ',_binary '\0','즉석밥 210g',1500,40,NULL),(18,'동원',_binary '\0','김치찌개 레토르트',3800,25,NULL),(19,'아몬드브리즈',_binary '\0','견과류 믹스 200g',8200,20,NULL),(20,'맥심',_binary '\0','카페라떼 캔커피',1800,60,NULL);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_image`
--

DROP TABLE IF EXISTS `product_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `extension` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `path` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `uuid` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_image`
--

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_image`
--

DROP TABLE IF EXISTS `profile_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `extension` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `path` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `uuid` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_image`
--

LOCK TABLES `profile_image` WRITE;
/*!40000 ALTER TABLE `profile_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quest`
--

DROP TABLE IF EXISTS `quest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quest` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `type` enum('ATTENDANCE','MISSION_REWARD','PRAISE_COMMENT','QUEST_CLEAR','WRITE_DIARY') COLLATE utf8mb4_general_ci NOT NULL,
  `url` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quest`
--

LOCK TABLES `quest` WRITE;
/*!40000 ALTER TABLE `quest` DISABLE KEYS */;
/*!40000 ALTER TABLE `quest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_diary`
--

DROP TABLE IF EXISTS `study_diary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study_diary` (
  `studydiary_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `content` longtext COLLATE utf8mb4_general_ci,
  `is_created` bit(1) NOT NULL,
  `like_count` int NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`studydiary_id`),
  KEY `FKinmedg0mq0vq0ntvnvmmcogtx` (`user_id`),
  CONSTRAINT `FKinmedg0mq0vq0ntvnvmmcogtx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_diary`
--

LOCK TABLES `study_diary` WRITE;
/*!40000 ALTER TABLE `study_diary` DISABLE KEYS */;
INSERT INTO `study_diary` VALUES (1,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘부터 Spring Boot를 이용한 웹 개발 프로젝트를 시작했다. MVC 패턴과 의존성 주입에 대해 배웠고, 간단한 Controller를 만들어 Hello World를 출력해보았다. 처음에는 어려웠지만 차근차근 따라하니 이해가 되기 시작했다.',_binary '',1,'Spring Boot 첫 프로젝트 시작',1),(2,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','useState와 useEffect Hook에 대해 깊이 공부했다. 함수형 컴포넌트에서 상태 관리하는 방법과 생명주기를 다루는 방법을 익혔다. 특히 useEffect의 의존성 배열 개념이 중요하다는 것을 깨달았다.',_binary '',2,'React Hook 정리',2),(3,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','자바의 스트림 API를 이용해서 컬렉션 데이터를 처리하는 방법을 학습했다. map, filter, reduce 등의 메서드를 사용하여 함수형 프로그래밍 스타일로 코드를 작성해보았다. 코드가 훨씬 간결해졌다.',_binary '',3,'Java 스트림 API 활용',3),(4,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CSS 레이아웃을 위한 Grid와 Flexbox의 차이점을 정리했다. 1차원 레이아웃에는 Flexbox가, 2차원 레이아웃에는 Grid가 적합하다는 것을 실습을 통해 확인했다.',_binary '',4,'CSS Grid와 Flexbox 비교',4),(5,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘 백준에서 DP 문제를 풀었다. 처음에는 접근 방법을 찾지 못해 막막했지만, 작은 부분 문제로 나누어 생각하니 해결할 수 있었다. 점화식을 세우는 연습이 더 필요하다.',_binary '',5,'알고리즘 문제 해결 과정',5),(6,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터베이스 성능 향상을 위한 인덱스 사용법을 공부했다. 복합 인덱스와 단일 인덱스의 차이점, 그리고 실행 계획을 통해 쿼리 최적화하는 방법을 배웠다.',_binary '',6,'MySQL 인덱스 최적화',6),(7,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Git Flow와 GitHub Flow에 대해 비교 분석했다. 팀 프로젝트에서 효율적인 브랜치 관리 전략의 중요성을 깨달았다. 실제 프로젝트에 적용해보고 싶다.',_binary '',7,'Git 브랜치 전략 학습',7),(8,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','TypeScript의 강력한 타입 시스템을 공부했다. 인터페이스, 제네릭, 유니온 타입 등을 활용하여 더 안전한 코드를 작성하는 방법을 익혔다.',_binary '',8,'TypeScript 타입 시스템',8),(9,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Docker를 이용한 컨테이너화에 대해 배웠다. Dockerfile 작성법과 이미지 빌드 과정을 실습했다. 개발 환경 통일에 매우 유용할 것 같다.',_binary '',9,'Docker 컨테이너 기초',9),(10,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','RESTful API 설계 원칙과 HTTP 메서드 사용법을 정리했다. 리소스 중심의 URL 설계와 상태 코드 활용법을 배웠다.',_binary '',10,'REST API 설계 원칙',10),(11,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Pandas와 NumPy를 이용한 데이터 분석 기초를 학습했다. CSV 파일을 읽어와 기본적인 통계 분석을 해보았다. 데이터 시각화도 배워보고 싶다.',_binary '',11,'Python 데이터 분석 시작',11),(12,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','JPA에서 엔티티 간의 연관관계 매핑을 공부했다. OneToMany, ManyToOne 등의 관계를 실제 코드로 구현해보며 이해를 높였다.',_binary '',12,'JPA 연관관계 매핑',12),(13,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Promise와 async/await를 이용한 비동기 처리 방법을 깊이 있게 공부했다. 콜백 지옥을 해결하는 우아한 방법을 배웠다.',_binary '',13,'JavaScript 비동기 처리',13),(14,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Vue.js에서 부모-자식 컴포넌트 간의 데이터 통신 방법을 학습했다. props와 emit을 활용한 양방향 통신을 실습했다.',_binary '',14,'Vue.js 컴포넌트 통신',14),(15,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','처음으로 AWS EC2에 웹 애플리케이션을 배포해보았다. 인스턴스 생성부터 도메인 연결까지 전 과정을 경험했다. 클라우드의 편리함을 실감했다.',_binary '',15,'AWS EC2 서버 배포',15),(16,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','React에서 Redux를 이용한 전역 상태 관리를 공부했다. 액션, 리듀서, 스토어의 개념과 데이터 플로우를 이해했다.',_binary '',16,'Redux 상태 관리',16),(17,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','프로그래머스에서 문자열 처리 문제를 풀었다. 정규표현식을 활용하여 효율적으로 해결할 수 있었다. 문제 해결 능력이 늘고 있는 것 같다.',_binary '',17,'코딩 테스트 문제 풀이',17),(18,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','NoSQL 데이터베이스인 MongoDB를 처음 다뤄보았다. 관계형 DB와는 다른 document 기반의 데이터 모델링을 배웠다.',_binary '',18,'MongoDB NoSQL 기초',18),(19,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Node.js와 Express를 이용해 간단한 웹 서버를 만들어보았다. 라우팅과 미들웨어 개념을 실습을 통해 익혔다.',_binary '',19,'Node.js Express 서버',19),(20,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CSS3 애니메이션과 transition을 이용해 동적인 웹 페이지를 만들어보았다. keyframes를 활용한 복잡한 애니메이션도 도전해봤다.',_binary '',20,'CSS 애니메이션 구현',20),(21,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Spring Security를 이용한 사용자 인증 시스템을 구현했다. JWT 토큰 기반 인증 방식을 적용해보았다.',_binary '',1,'Spring Security 인증',21),(22,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','React Router를 이용한 SPA 페이지 라우팅을 공부했다. 동적 라우팅과 중첩 라우팅을 구현해보았다.',_binary '',2,'React Router 페이지 관리',22),(23,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','기본 자료구조인 스택과 큐의 개념과 구현 방법을 복습했다. 각각의 특성과 활용 사례를 정리했다.',_binary '',3,'자료구조 스택과 큐',23),(24,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Webpack을 이용한 모듈 번들링에 대해 학습했다. entry, output, loader, plugin의 개념을 익혔다.',_binary '',4,'Webpack 모듈 번들링',24),(25,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','복잡한 SQL 쿼리의 성능을 개선하는 방법을 공부했다. 인덱스 활용과 쿼리 재작성 기법을 배웠다.',_binary '',5,'SQL 쿼리 최적화 기법',25),(26,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘부터 Spring Boot를 이용한 웹 개발 프로젝트를 시작했다. MVC 패턴과 의존성 주입에 대해 배웠고, 간단한 Controller를 만들어 Hello World를 출력해보았다. 처음에는 어려웠지만 차근차근 따라하니 이해가 되기 시작했다.',_binary '',6,'GraphQL API 설계',26),(27,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','useState와 useEffect Hook에 대해 깊이 공부했다. 함수형 컴포넌트에서 상태 관리하는 방법과 생명주기를 다루는 방법을 익혔다. 특히 useEffect의 의존성 배열 개념이 중요하다는 것을 깨달았다.',_binary '',7,'Linux 명령어 정리',27),(28,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','자바의 스트림 API를 이용해서 컬렉션 데이터를 처리하는 방법을 학습했다. map, filter, reduce 등의 메서드를 사용하여 함수형 프로그래밍 스타일로 코드를 작성해보았다. 코드가 훨씬 간결해졌다.',_binary '',8,'TDD 테스트 주도 개발',28),(29,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CSS 레이아웃을 위한 Grid와 Flexbox의 차이점을 정리했다. 1차원 레이아웃에는 Flexbox가, 2차원 레이아웃에는 Grid가 적합하다는 것을 실습을 통해 확인했다.',_binary '',9,'Sass CSS 전처리기',29),(30,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘 백준에서 DP 문제를 풀었다. 처음에는 접근 방법을 찾지 못해 막막했지만, 작은 부분 문제로 나누어 생각하니 해결할 수 있었다. 점화식을 세우는 연습이 더 필요하다.',_binary '',10,'Firebase 실시간 데이터베이스',30),(31,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터베이스 성능 향상을 위한 인덱스 사용법을 공부했다. 복합 인덱스와 단일 인덱스의 차이점, 그리고 실행 계획을 통해 쿼리 최적화하는 방법을 배웠다.',_binary '',11,'객체지향 프로그래밍 원칙',31),(32,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Git Flow와 GitHub Flow에 대해 비교 분석했다. 팀 프로젝트에서 효율적인 브랜치 관리 전략의 중요성을 깨달았다. 실제 프로젝트에 적용해보고 싶다.',_binary '',12,'HTTP 프로토콜 이해',32),(33,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','TypeScript의 강력한 타입 시스템을 공부했다. 인터페이스, 제네릭, 유니온 타입 등을 활용하여 더 안전한 코드를 작성하는 방법을 익혔다.',_binary '',13,'디자인 패턴 공부',33),(34,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Docker를 이용한 컨테이너화에 대해 배웠다. Dockerfile 작성법과 이미지 빌드 과정을 실습했다. 개발 환경 통일에 매우 유용할 것 같다.',_binary '',14,'Git 고급 기능 활용',34),(35,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','RESTful API 설계 원칙과 HTTP 메서드 사용법을 정리했다. 리소스 중심의 URL 설계와 상태 코드 활용법을 배웠다.',_binary '',15,'함수형 프로그래밍 개념',35),(36,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Pandas와 NumPy를 이용한 데이터 분석 기초를 학습했다. CSV 파일을 읽어와 기본적인 통계 분석을 해보았다. 데이터 시각화도 배워보고 싶다.',_binary '',16,'Redis 캐싱 전략',36),(37,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','JPA에서 엔티티 간의 연관관계 매핑을 공부했다. OneToMany, ManyToOne 등의 관계를 실제 코드로 구현해보며 이해를 높였다.',_binary '',17,'React Hooks 심화',37),(38,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Promise와 async/await를 이용한 비동기 처리 방법을 깊이 있게 공부했다. 콜백 지옥을 해결하는 우아한 방법을 배웠다.',_binary '',18,'API 설계 베스트 프랙티스',38),(39,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Vue.js에서 부모-자식 컴포넌트 간의 데이터 통신 방법을 학습했다. props와 emit을 활용한 양방향 통신을 실습했다.',_binary '',19,'Docker Compose 활용',39),(40,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','처음으로 AWS EC2에 웹 애플리케이션을 배포해보았다. 인스턴스 생성부터 도메인 연결까지 전 과정을 경험했다. 클라우드의 편리함을 실감했다.',_binary '',20,'TypeScript 고급 타입',40),(41,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','React에서 Redux를 이용한 전역 상태 관리를 공부했다. 액션, 리듀서, 스토어의 개념과 데이터 플로우를 이해했다.',_binary '',1,'Vue 3 Composition API',41),(42,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','프로그래머스에서 문자열 처리 문제를 풀었다. 정규표현식을 활용하여 효율적으로 해결할 수 있었다. 문제 해결 능력이 늘고 있는 것 같다.',_binary '',2,'웹 성능 최적화 기법',42),(43,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','NoSQL 데이터베이스인 MongoDB를 처음 다뤄보았다. 관계형 DB와는 다른 document 기반의 데이터 모델링을 배웠다.',_binary '',3,'GraphQL 스키마 설계',43),(44,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Node.js와 Express를 이용해 간단한 웹 서버를 만들어보았다. 라우팅과 미들웨어 개념을 실습을 통해 익혔다.',_binary '',4,'Kubernetes 기초',44),(45,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CSS3 애니메이션과 transition을 이용해 동적인 웹 페이지를 만들어보았다. keyframes를 활용한 복잡한 애니메이션도 도전해봤다.',_binary '',5,'Next.js SSR 구현',45),(46,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Spring Security를 이용한 사용자 인증 시스템을 구현했다. JWT 토큰 기반 인증 방식을 적용해보았다.',_binary '',6,'Jest 단위 테스트',46),(47,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','React Router를 이용한 SPA 페이지 라우팅을 공부했다. 동적 라우팅과 중첩 라우팅을 구현해보았다.',_binary '',7,'알고리즘 시간복잡도',47),(48,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','기본 자료구조인 스택과 큐의 개념과 구현 방법을 복습했다. 각각의 특성과 활용 사례를 정리했다.',_binary '',8,'Express 미들웨어 개발',48),(49,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Webpack을 이용한 모듈 번들링에 대해 학습했다. entry, output, loader, plugin의 개념을 익혔다.',_binary '',9,'CSS Grid 고급 레이아웃',49),(50,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','복잡한 SQL 쿼리의 성능을 개선하는 방법을 공부했다. 인덱스 활용과 쿼리 재작성 기법을 배웠다.',_binary '',10,'PWA 개발 기초',50),(51,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘부터 Spring Boot를 이용한 웹 개발 프로젝트를 시작했다. MVC 패턴과 의존성 주입에 대해 배웠고, 간단한 Controller를 만들어 Hello World를 출력해보았다. 처음에는 어려웠지만 차근차근 따라하니 이해가 되기 시작했다.',_binary '',11,'Spring Boot 첫 프로젝트 시작',1),(52,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','useState와 useEffect Hook에 대해 깊이 공부했다. 함수형 컴포넌트에서 상태 관리하는 방법과 생명주기를 다루는 방법을 익혔다. 특히 useEffect의 의존성 배열 개념이 중요하다는 것을 깨달았다.',_binary '',12,'React Hook 정리',2),(53,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','자바의 스트림 API를 이용해서 컬렉션 데이터를 처리하는 방법을 학습했다. map, filter, reduce 등의 메서드를 사용하여 함수형 프로그래밍 스타일로 코드를 작성해보았다. 코드가 훨씬 간결해졌다.',_binary '',13,'Java 스트림 API 활용',3),(54,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CSS 레이아웃을 위한 Grid와 Flexbox의 차이점을 정리했다. 1차원 레이아웃에는 Flexbox가, 2차원 레이아웃에는 Grid가 적합하다는 것을 실습을 통해 확인했다.',_binary '',14,'CSS Grid와 Flexbox 비교',4),(55,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘 백준에서 DP 문제를 풀었다. 처음에는 접근 방법을 찾지 못해 막막했지만, 작은 부분 문제로 나누어 생각하니 해결할 수 있었다. 점화식을 세우는 연습이 더 필요하다.',_binary '',15,'알고리즘 문제 해결 과정',5),(56,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터베이스 성능 향상을 위한 인덱스 사용법을 공부했다. 복합 인덱스와 단일 인덱스의 차이점, 그리고 실행 계획을 통해 쿼리 최적화하는 방법을 배웠다.',_binary '',16,'MySQL 인덱스 최적화',6),(57,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Git Flow와 GitHub Flow에 대해 비교 분석했다. 팀 프로젝트에서 효율적인 브랜치 관리 전략의 중요성을 깨달았다. 실제 프로젝트에 적용해보고 싶다.',_binary '',17,'Git 브랜치 전략 학습',7),(58,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','TypeScript의 강력한 타입 시스템을 공부했다. 인터페이스, 제네릭, 유니온 타입 등을 활용하여 더 안전한 코드를 작성하는 방법을 익혔다.',_binary '',18,'TypeScript 타입 시스템',8),(59,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Docker를 이용한 컨테이너화에 대해 배웠다. Dockerfile 작성법과 이미지 빌드 과정을 실습했다. 개발 환경 통일에 매우 유용할 것 같다.',_binary '',19,'Docker 컨테이너 기초',9),(60,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','RESTful API 설계 원칙과 HTTP 메서드 사용법을 정리했다. 리소스 중심의 URL 설계와 상태 코드 활용법을 배웠다.',_binary '',20,'REST API 설계 원칙',10),(61,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Pandas와 NumPy를 이용한 데이터 분석 기초를 학습했다. CSV 파일을 읽어와 기본적인 통계 분석을 해보았다. 데이터 시각화도 배워보고 싶다.',_binary '',1,'Python 데이터 분석 시작',11),(62,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','JPA에서 엔티티 간의 연관관계 매핑을 공부했다. OneToMany, ManyToOne 등의 관계를 실제 코드로 구현해보며 이해를 높였다.',_binary '',2,'JPA 연관관계 매핑',12),(63,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Promise와 async/await를 이용한 비동기 처리 방법을 깊이 있게 공부했다. 콜백 지옥을 해결하는 우아한 방법을 배웠다.',_binary '',3,'JavaScript 비동기 처리',13),(64,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Vue.js에서 부모-자식 컴포넌트 간의 데이터 통신 방법을 학습했다. props와 emit을 활용한 양방향 통신을 실습했다.',_binary '',4,'Vue.js 컴포넌트 통신',14),(65,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','처음으로 AWS EC2에 웹 애플리케이션을 배포해보았다. 인스턴스 생성부터 도메인 연결까지 전 과정을 경험했다. 클라우드의 편리함을 실감했다.',_binary '',5,'AWS EC2 서버 배포',15),(66,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','React에서 Redux를 이용한 전역 상태 관리를 공부했다. 액션, 리듀서, 스토어의 개념과 데이터 플로우를 이해했다.',_binary '',6,'Redux 상태 관리',16),(67,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','프로그래머스에서 문자열 처리 문제를 풀었다. 정규표현식을 활용하여 효율적으로 해결할 수 있었다. 문제 해결 능력이 늘고 있는 것 같다.',_binary '',7,'코딩 테스트 문제 풀이',17),(68,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','NoSQL 데이터베이스인 MongoDB를 처음 다뤄보았다. 관계형 DB와는 다른 document 기반의 데이터 모델링을 배웠다.',_binary '',8,'MongoDB NoSQL 기초',18),(69,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Node.js와 Express를 이용해 간단한 웹 서버를 만들어보았다. 라우팅과 미들웨어 개념을 실습을 통해 익혔다.',_binary '',9,'Node.js Express 서버',19),(70,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CSS3 애니메이션과 transition을 이용해 동적인 웹 페이지를 만들어보았다. keyframes를 활용한 복잡한 애니메이션도 도전해봤다.',_binary '',10,'CSS 애니메이션 구현',20),(71,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Spring Security를 이용한 사용자 인증 시스템을 구현했다. JWT 토큰 기반 인증 방식을 적용해보았다.',_binary '',11,'Spring Security 인증',21),(72,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','React Router를 이용한 SPA 페이지 라우팅을 공부했다. 동적 라우팅과 중첩 라우팅을 구현해보았다.',_binary '',12,'React Router 페이지 관리',22),(73,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','기본 자료구조인 스택과 큐의 개념과 구현 방법을 복습했다. 각각의 특성과 활용 사례를 정리했다.',_binary '',13,'자료구조 스택과 큐',23),(74,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Webpack을 이용한 모듈 번들링에 대해 학습했다. entry, output, loader, plugin의 개념을 익혔다.',_binary '',14,'Webpack 모듈 번들링',24),(75,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','복잡한 SQL 쿼리의 성능을 개선하는 방법을 공부했다. 인덱스 활용과 쿼리 재작성 기법을 배웠다.',_binary '',15,'SQL 쿼리 최적화 기법',25),(76,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘부터 Spring Boot를 이용한 웹 개발 프로젝트를 시작했다. MVC 패턴과 의존성 주입에 대해 배웠고, 간단한 Controller를 만들어 Hello World를 출력해보았다. 처음에는 어려웠지만 차근차근 따라하니 이해가 되기 시작했다.',_binary '',16,'GraphQL API 설계',26),(77,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','useState와 useEffect Hook에 대해 깊이 공부했다. 함수형 컴포넌트에서 상태 관리하는 방법과 생명주기를 다루는 방법을 익혔다. 특히 useEffect의 의존성 배열 개념이 중요하다는 것을 깨달았다.',_binary '',17,'Linux 명령어 정리',27),(78,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','자바의 스트림 API를 이용해서 컬렉션 데이터를 처리하는 방법을 학습했다. map, filter, reduce 등의 메서드를 사용하여 함수형 프로그래밍 스타일로 코드를 작성해보았다. 코드가 훨씬 간결해졌다.',_binary '',18,'TDD 테스트 주도 개발',28),(79,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CSS 레이아웃을 위한 Grid와 Flexbox의 차이점을 정리했다. 1차원 레이아웃에는 Flexbox가, 2차원 레이아웃에는 Grid가 적합하다는 것을 실습을 통해 확인했다.',_binary '',19,'Sass CSS 전처리기',29),(80,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','오늘 백준에서 DP 문제를 풀었다. 처음에는 접근 방법을 찾지 못해 막막했지만, 작은 부분 문제로 나누어 생각하니 해결할 수 있었다. 점화식을 세우는 연습이 더 필요하다.',_binary '',20,'Firebase 실시간 데이터베이스',30),(81,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터베이스 성능 향상을 위한 인덱스 사용법을 공부했다. 복합 인덱스와 단일 인덱스의 차이점, 그리고 실행 계획을 통해 쿼리 최적화하는 방법을 배웠다.',_binary '',1,'객체지향 프로그래밍 원칙',31),(82,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Git Flow와 GitHub Flow에 대해 비교 분석했다. 팀 프로젝트에서 효율적인 브랜치 관리 전략의 중요성을 깨달았다. 실제 프로젝트에 적용해보고 싶다.',_binary '',2,'HTTP 프로토콜 이해',32),(83,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','TypeScript의 강력한 타입 시스템을 공부했다. 인터페이스, 제네릭, 유니온 타입 등을 활용하여 더 안전한 코드를 작성하는 방법을 익혔다.',_binary '',3,'디자인 패턴 공부',33),(84,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Docker를 이용한 컨테이너화에 대해 배웠다. Dockerfile 작성법과 이미지 빌드 과정을 실습했다. 개발 환경 통일에 매우 유용할 것 같다.',_binary '',4,'Git 고급 기능 활용',34),(85,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','RESTful API 설계 원칙과 HTTP 메서드 사용법을 정리했다. 리소스 중심의 URL 설계와 상태 코드 활용법을 배웠다.',_binary '',5,'함수형 프로그래밍 개념',35),(86,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Pandas와 NumPy를 이용한 데이터 분석 기초를 학습했다. CSV 파일을 읽어와 기본적인 통계 분석을 해보았다. 데이터 시각화도 배워보고 싶다.',_binary '',6,'Redis 캐싱 전략',36),(87,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','JPA에서 엔티티 간의 연관관계 매핑을 공부했다. OneToMany, ManyToOne 등의 관계를 실제 코드로 구현해보며 이해를 높였다.',_binary '',7,'React Hooks 심화',37),(88,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Promise와 async/await를 이용한 비동기 처리 방법을 깊이 있게 공부했다. 콜백 지옥을 해결하는 우아한 방법을 배웠다.',_binary '',8,'API 설계 베스트 프랙티스',38),(89,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Vue.js에서 부모-자식 컴포넌트 간의 데이터 통신 방법을 학습했다. props와 emit을 활용한 양방향 통신을 실습했다.',_binary '',9,'Docker Compose 활용',39),(90,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','처음으로 AWS EC2에 웹 애플리케이션을 배포해보았다. 인스턴스 생성부터 도메인 연결까지 전 과정을 경험했다. 클라우드의 편리함을 실감했다.',_binary '',10,'TypeScript 고급 타입',40),(91,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','React에서 Redux를 이용한 전역 상태 관리를 공부했다. 액션, 리듀서, 스토어의 개념과 데이터 플로우를 이해했다.',_binary '',11,'Vue 3 Composition API',41),(92,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','프로그래머스에서 문자열 처리 문제를 풀었다. 정규표현식을 활용하여 효율적으로 해결할 수 있었다. 문제 해결 능력이 늘고 있는 것 같다.',_binary '',12,'웹 성능 최적화 기법',42),(93,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','NoSQL 데이터베이스인 MongoDB를 처음 다뤄보았다. 관계형 DB와는 다른 document 기반의 데이터 모델링을 배웠다.',_binary '',13,'GraphQL 스키마 설계',43),(94,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Node.js와 Express를 이용해 간단한 웹 서버를 만들어보았다. 라우팅과 미들웨어 개념을 실습을 통해 익혔다.',_binary '',14,'Kubernetes 기초',44),(95,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','CSS3 애니메이션과 transition을 이용해 동적인 웹 페이지를 만들어보았다. keyframes를 활용한 복잡한 애니메이션도 도전해봤다.',_binary '',15,'Next.js SSR 구현',45),(96,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Spring Security를 이용한 사용자 인증 시스템을 구현했다. JWT 토큰 기반 인증 방식을 적용해보았다.',_binary '',16,'Jest 단위 테스트',46),(97,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','React Router를 이용한 SPA 페이지 라우팅을 공부했다. 동적 라우팅과 중첩 라우팅을 구현해보았다.',_binary '',17,'알고리즘 시간복잡도',47),(98,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','기본 자료구조인 스택과 큐의 개념과 구현 방법을 복습했다. 각각의 특성과 활용 사례를 정리했다.',_binary '',18,'Express 미들웨어 개발',48),(99,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Webpack을 이용한 모듈 번들링에 대해 학습했다. entry, output, loader, plugin의 개념을 익혔다.',_binary '',19,'CSS Grid 고급 레이아웃',49),(100,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','복잡한 SQL 쿼리의 성능을 개선하는 방법을 공부했다. 인덱스 활용과 쿼리 재작성 기법을 배웠다.',_binary '',20,'PWA 개발 기초',50);
/*!40000 ALTER TABLE `study_diary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_diary_comment`
--

DROP TABLE IF EXISTS `study_diary_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study_diary_comment` (
  `studydiary_comment_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `content` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `studydiary_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`studydiary_comment_id`),
  KEY `FKg5emh5wnbyyfr5hjimgpvoa25` (`studydiary_id`),
  KEY `FKsbyr1ee9tqss08cb7sw0s14s5` (`user_id`),
  CONSTRAINT `FKg5emh5wnbyyfr5hjimgpvoa25` FOREIGN KEY (`studydiary_id`) REFERENCES `study_diary` (`studydiary_id`),
  CONSTRAINT `FKsbyr1ee9tqss08cb7sw0s14s5` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_diary_comment`
--

LOCK TABLES `study_diary_comment` WRITE;
/*!40000 ALTER TABLE `study_diary_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_diary_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_diary_like`
--

DROP TABLE IF EXISTS `study_diary_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study_diary_like` (
  `studydiary_like_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `studydiary_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`studydiary_like_id`),
  KEY `FK2ey22pojp2nvyaef0gw8jgsej` (`studydiary_id`),
  KEY `FKi1wes972pxndvu1yib426c10o` (`user_id`),
  CONSTRAINT `FK2ey22pojp2nvyaef0gw8jgsej` FOREIGN KEY (`studydiary_id`) REFERENCES `study_diary` (`studydiary_id`),
  CONSTRAINT `FKi1wes972pxndvu1yib426c10o` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_diary_like`
--

LOCK TABLES `study_diary_like` WRITE;
/*!40000 ALTER TABLE `study_diary_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_diary_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `submission`
--

DROP TABLE IF EXISTS `submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `submission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` text COLLATE utf8mb4_general_ci NOT NULL,
  `feedback` text COLLATE utf8mb4_general_ci,
  `file_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `original_file_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_mission_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKb6wnpgav0mfk6r66bra46cbu9` (`user_mission_id`),
  CONSTRAINT `FKgau51x0ru8en3m11ppwwyawco` FOREIGN KEY (`user_mission_id`) REFERENCES `user_mission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `submission`
--

LOCK TABLES `submission` WRITE;
/*!40000 ALTER TABLE `submission` DISABLE KEYS */;
/*!40000 ALTER TABLE `submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_mission`
--

DROP TABLE IF EXISTS `user_mission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_mission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `progress` enum('ABORTED','COMPLETED','FEEDBACK_COMPLETED','IN_FEEDBACK','IN_PROGRESS','NOT_STARTED','REWARD_RECEIVED') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mission_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `user_mission_group_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKo6d4kbe65tpq62e3lxir0qpf` (`user_id`,`mission_id`,`user_mission_group_id`),
  KEY `FKdlc6c9h0rifeykrviy3fgmygv` (`mission_id`),
  KEY `FKlp6r6mrt02hbss1c9na0odvnd` (`user_mission_group_id`),
  CONSTRAINT `FK5sl9s3v4lwwwwxmphfdmlvv8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKdlc6c9h0rifeykrviy3fgmygv` FOREIGN KEY (`mission_id`) REFERENCES `mission` (`id`),
  CONSTRAINT `FKlp6r6mrt02hbss1c9na0odvnd` FOREIGN KEY (`user_mission_group_id`) REFERENCES `user_mission_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_mission`
--

LOCK TABLES `user_mission` WRITE;
/*!40000 ALTER TABLE `user_mission` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_mission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_mission_group`
--

DROP TABLE IF EXISTS `user_mission_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_mission_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `mission_group_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKshd94wcdiwphcevu2ugfj4vka` (`user_id`,`mission_group_id`),
  KEY `FK7om4flv8snoaw4mfrq6l2y5xw` (`mission_group_id`),
  CONSTRAINT `FK7om4flv8snoaw4mfrq6l2y5xw` FOREIGN KEY (`mission_group_id`) REFERENCES `mission_group` (`id`),
  CONSTRAINT `FKbq7qkgmouce0tiyme2n6k8t6j` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_mission_group`
--

LOCK TABLES `user_mission_group` WRITE;
/*!40000 ALTER TABLE `user_mission_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_mission_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_mission_state_log`
--

DROP TABLE IF EXISTS `user_mission_state_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_mission_state_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `note` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `next_state` enum('ABORTED','COMPLETED','FEEDBACK_COMPLETED','IN_FEEDBACK','IN_PROGRESS','NOT_STARTED','REWARD_RECEIVED') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `prev_state` enum('ABORTED','COMPLETED','FEEDBACK_COMPLETED','IN_FEEDBACK','IN_PROGRESS','NOT_STARTED','REWARD_RECEIVED') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_mission_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKm59gassuwik5y7bqn2ley44fg` (`user_mission_id`),
  CONSTRAINT `FKm59gassuwik5y7bqn2ley44fg` FOREIGN KEY (`user_mission_id`) REFERENCES `user_mission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_mission_state_log`
--

LOCK TABLES `user_mission_state_log` WRITE;
/*!40000 ALTER TABLE `user_mission_state_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_mission_state_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_mission_task`
--

DROP TABLE IF EXISTS `user_mission_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_mission_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `state` enum('COMPLETED','IN_PROGRESS','NOT_STARTED') COLLATE utf8mb4_general_ci NOT NULL,
  `mission_task_id` bigint NOT NULL,
  `user_mission_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2idtvsjfim5rryln9i0y27gk` (`mission_task_id`),
  KEY `FK63pf8dtwhn2wp9idj0bftlg44` (`user_mission_id`),
  CONSTRAINT `FK2idtvsjfim5rryln9i0y27gk` FOREIGN KEY (`mission_task_id`) REFERENCES `mission_task` (`id`),
  CONSTRAINT `FK63pf8dtwhn2wp9idj0bftlg44` FOREIGN KEY (`user_mission_id`) REFERENCES `user_mission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_mission_task`
--

LOCK TABLES `user_mission_task` WRITE;
/*!40000 ALTER TABLE `user_mission_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_mission_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_quest`
--

DROP TABLE IF EXISTS `user_quest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_quest` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_completable` bit(1) NOT NULL,
  `is_completed` bit(1) NOT NULL,
  `quest_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmlculhppmv80bbt4qjlqonn39` (`quest_id`),
  KEY `FKbcdo2fitudvvw1bltotqnrbtl` (`user_id`),
  CONSTRAINT `FKbcdo2fitudvvw1bltotqnrbtl` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmlculhppmv80bbt4qjlqonn39` FOREIGN KEY (`quest_id`) REFERENCES `quest` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_quest`
--

LOCK TABLES `user_quest` WRITE;
/*!40000 ALTER TABLE `user_quest` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_quest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `modified_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `exp` int NOT NULL,
  `name` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(60) COLLATE utf8mb4_general_ci NOT NULL,
  `phone_number` varchar(13) COLLATE utf8mb4_general_ci NOT NULL,
  `point` int NOT NULL,
  `role` enum('ADMIN','LECTURER','USER') COLLATE utf8mb4_general_ci NOT NULL,
  `username` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,
  `profile_image_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9q63snka3mdh91as4io72espi` (`phone_number`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UK4unapofvpijp79n4j3sheoun7` (`profile_image_id`),
  CONSTRAINT `FKgjuyyw52s3xe61nrl2l6q1j6y` FOREIGN KEY (`profile_image_id`) REFERENCES `profile_image` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2025-06-21 00:00:00.000000','2025-07-21 08:07:07.000000',NULL,5000,'최현','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-4567-8901',45,'USER','choihyun',NULL),(2,'2025-03-21 00:00:00.000000','2025-07-21 08:07:07.000000','알고리즘 문제 풀이에 빠져있습니다.',380,'정수영','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-5678-9012',670,'USER','jungsooyoung',NULL),(3,'2025-04-21 00:00:00.000000','2025-07-21 08:07:07.000000','데이터베이스 공부 중입니다.',150,'강도현','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-6789-0123',290,'USER','kangdohyeon',NULL),(4,'2025-06-22 00:00:00.000000','2025-07-21 08:07:07.000000','관리자입니다.',480,'이재원','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-7890-1234',950,'ADMIN','leejaewon',NULL),(5,'2025-06-11 00:00:00.000000','2025-07-21 08:07:07.000000','React 공부 시작했어요!',65,'김소연','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-8901-2345',120,'USER','kimsoyeon',NULL),(6,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,220,'박태현','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-9012-3456',410,'USER','parktaehyun',NULL),(7,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Spring Boot 개발자 희망!',310,'최유나','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-0123-4567',580,'USER','choiyuna',NULL),(8,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','Python 강사입니다.',390,'장민호','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-1357-2468',720,'LECTURER','jangminho',NULL),(9,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','신입 개발자입니다.',95,'윤슬기','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-2468-1357',180,'USER','yoonseulgi',NULL),(10,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,170,'서경민','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-3579-2460',340,'USER','seokyungmin',NULL),(11,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','웹 개발 공부 중입니다.',240,'임지우','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-4680-1379',450,'USER','limjiwoo',NULL),(12,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','코딩 테스트 준비 중!',110,'홍민석','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-5791-2468',200,'USER','hongminseok',NULL),(13,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,350,'백지연','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-6802-3571',620,'USER','baekjiyeon',NULL),(14,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','DevOps에 관심이 많습니다.',200,'오승환','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-7913-4682',380,'USER','ohseunghwan',NULL),(15,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','초보 개발자예요.',45,'송민지','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-8024-5793',90,'USER','songminji',NULL),(16,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','JavaScript 전문 강사입니다.',410,'권혁진','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-9135-6804',780,'LECTURER','kwonhyukjin',NULL),(17,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','UI/UX 디자인도 공부해요.',280,'한소민','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-0246-7915',520,'USER','hansomin',NULL),(18,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,135,'유상욱','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-1358-8026',260,'USER','yoosangwook',NULL),(19,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','모바일 앱 개발 공부 중!',260,'이승빈','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-2469-9137',490,'USER','leeseungbin',NULL),(20,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','게임 개발자가 되고 싶어요.',85,'박진수','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-3570-0248',160,'USER','parkjinsoo',NULL),(21,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,370,'김다혜','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-4681-1359',710,'USER','kimdahye',NULL),(22,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','백엔드 API 개발 전문가 목표!',180,'최준호','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-5792-2460',350,'USER','choijunho',NULL),(23,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터 사이언스 공부해요.',70,'정하은','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-6803-3571',130,'USER','jeonghaeun',NULL),(24,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,230,'신동혁','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-7914-4682',430,'USER','shindonghyuk',NULL),(25,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','풀스택 개발자 꿈꿔요!',300,'김은비','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-8025-5793',560,'USER','kimeunbi',NULL),(26,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','프로그래밍 초보입니다.',40,'이광수','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-9136-6804',80,'USER','leekwangsoo',NULL),(27,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,340,'박수영','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-0247-7915',640,'USER','parksooyoung',NULL),(28,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','AI/ML 강사입니다.',450,'최혜성','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-1359-8026',890,'LECTURER','choihyeseong',NULL),(29,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','블록체인 기술에 관심 많아요.',140,'장은지','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-2460-9137',270,'USER','jangeunji',NULL),(30,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,210,'윤찬희','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-3571-0248',400,'USER','yoonchanhee',NULL),(31,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','네트워크 보안 공부 중!',100,'한지호','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-4682-1359',190,'USER','hanjiho',NULL),(32,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','클라우드 컴퓨팅 전문가 희망!',250,'이민지','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-5793-2460',480,'USER','leeminji',NULL),(33,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,165,'박승준','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-6804-3571',320,'USER','parkseungjun',NULL),(34,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','사이버보안 전문가가 될 거예요.',380,'김정수','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-7915-4682',750,'USER','kimjeongsoo',NULL),(35,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','웹 퍼블리셔 공부해요.',55,'최예원','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-8026-5793',110,'USER','choiyewon',NULL),(36,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,290,'정현진','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-9137-6804',540,'USER','jeonghyunjin',NULL),(37,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','임베디드 시스템 개발자 목표!',190,'유재환','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-0248-7915',360,'USER','yoojaehwan',NULL),(38,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','데이터 분석가가 되고 싶어요.',120,'강지수','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-1360-8026',220,'USER','kangjisu',NULL),(39,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,320,'임서영','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-2471-9137',600,'USER','limseoyoung',NULL),(40,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','로봇 공학 공부 중입니다.',75,'오동진','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-3582-0248',140,'USER','ohdongjin',NULL),(41,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','소프트웨어 아키텍트 목표!',245,'송민혜','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-4693-1359',470,'USER','songminhye',NULL),(42,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,155,'권승호','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-5704-2460',300,'USER','kwonseungho',NULL),(43,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','퀀트 개발자가 되고 싶어요!',360,'한지민','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-6815-3571',680,'USER','hanjimin',NULL),(44,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','게임 서버 개발자 희망!',125,'이준우','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-7926-4682',240,'USER','leejunwoo',NULL),(45,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,270,'박다연','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-8037-5793',510,'USER','parkdayeon',NULL),(46,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','VR/AR 개발에 관심이 많아요.',90,'김성민','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-9148-6804',170,'USER','kimseongmin',NULL),(47,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','핀테크 개발자가 될 거예요!',215,'최정훈','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-0259-7915',420,'USER','choijeonghoon',NULL),(48,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000',NULL,315,'정혜지','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-1360-8027',590,'USER','junghyeji',NULL),(49,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','UI 개발에 관심이 많습니다.',160,'신아라','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-9159-8026',310,'USER','shinara',NULL),(50,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','클린 아키텍처를 공부하고 있어요.',140,'이지환','$2a$10$6Kq.7bJvE4XGXV/1nYcHWOsQ7XhVNvUvNKKXzJ2QY3HZmE1AzF.2m','010-0260-9137',260,'USER','leejihwan',NULL),(51,'2025-07-21 08:07:07.000000','2025-07-21 08:07:07.000000','시스템 관리자입니다.',500,'관리자','$2a$10$t2OmMOInDxTUKBbltzp5ceYc11MqcnthlAGHWTSF3anEIT1M6vBX6','010-1111-2222',1000,'ADMIN','admin01',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-23 15:16:43
