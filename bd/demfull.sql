-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: dem
-- ------------------------------------------------------
-- Server version	8.0.30

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
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clients` (
  `client_id` int NOT NULL AUTO_INCREMENT,
  `client_name` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
INSERT INTO `clients` VALUES (1,'Иван Иванов','123-456-7890'),(2,'Мария Петрова','098-765-4321'),(5,'Яковлев Борис Николаевич','81234567890'),(6,'Коренко Дмитрий Николаевич','81234567890'),(7,'Козлов Андрей Александрович','80987654321'),(8,'asdasda','sadasdasd'),(9,'assdasd','asdasd'),(10,'we1ee','asdawdas'),(11,'afasfa','sfafaf'),(12,'111111111','11111111'),(13,'ddddddd','124124124'),(14,'aaaaaa','aaaaa'),(15,'asdasda','12412412412'),(16,'asdassda','12412412412');
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment_models`
--

DROP TABLE IF EXISTS `equipment_models`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment_models` (
  `model_id` int NOT NULL AUTO_INCREMENT,
  `model_name` varchar(100) NOT NULL,
  `equipment_type_id` int NOT NULL,
  PRIMARY KEY (`model_id`),
  KEY `equipment_type_id` (`equipment_type_id`),
  CONSTRAINT `equipment_models_ibfk_1` FOREIGN KEY (`equipment_type_id`) REFERENCES `equipment_types` (`equipment_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment_models`
--

LOCK TABLES `equipment_models` WRITE;
/*!40000 ALTER TABLE `equipment_models` DISABLE KEYS */;
INSERT INTO `equipment_models` VALUES (1,'Dell Inspiron',1),(2,'HP LaserJet',2),(3,'Canon Lide',3),(7,'Acer Aspire e5',8),(8,'Acer Aspire e5',8),(9,'iphone 12',10),(10,'assdasdad',2),(11,'dadsdasd',3),(12,'dasdasd',3),(13,'asf',3),(14,'11111',2),(15,'ddasd',2),(16,'aaaaa',2),(17,'sdasdasd',2),(18,'asadasdasd',1);
/*!40000 ALTER TABLE `equipment_models` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment_types`
--

DROP TABLE IF EXISTS `equipment_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment_types` (
  `equipment_type_id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(100) NOT NULL,
  PRIMARY KEY (`equipment_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment_types`
--

LOCK TABLES `equipment_types` WRITE;
/*!40000 ALTER TABLE `equipment_types` DISABLE KEYS */;
INSERT INTO `equipment_types` VALUES (1,'Компьютер'),(2,'Принтер'),(3,'Сканер'),(7,'Монитор'),(8,'Ноутбук'),(9,'Сетевое оборудование'),(10,'Телефон'),(11,'Факс'),(12,'Проектор'),(13,'Копировальный аппарат'),(14,'МФУ (Многофункциональное устройство)'),(15,'Сервер'),(16,'ИБП (Источники бесперебойного питания)'),(17,'Плоттер'),(18,'Модем'),(19,'Маршрутизатор'),(20,'Коммутатор'),(21,'Периферийное устройство'),(22,'Программное обеспечение'),(23,'Хранение данных (NAS/SAN)');
/*!40000 ALTER TABLE `equipment_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `requests`
--

DROP TABLE IF EXISTS `requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `requests` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `date_added` date NOT NULL,
  `model_id` int NOT NULL,
  `problem_description` text NOT NULL,
  `client_id` int NOT NULL,
  `worker_id` int DEFAULT NULL,
  `status` enum('Новая заявка','В процессе ремонта','Завершена') NOT NULL DEFAULT 'Новая заявка',
  `comments` text,
  `create_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `completion_time` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  UNIQUE KEY `request_id_UNIQUE` (`request_id`),
  KEY `model_id` (`model_id`),
  KEY `client_id` (`client_id`),
  KEY `worker_id` (`worker_id`),
  CONSTRAINT `requests_ibfk_1` FOREIGN KEY (`model_id`) REFERENCES `equipment_models` (`model_id`),
  CONSTRAINT `requests_ibfk_2` FOREIGN KEY (`client_id`) REFERENCES `clients` (`client_id`),
  CONSTRAINT `requests_ibfk_3` FOREIGN KEY (`worker_id`) REFERENCES `workers` (`worker_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `requests`
--

LOCK TABLES `requests` WRITE;
/*!40000 ALTER TABLE `requests` DISABLE KEYS */;
INSERT INTO `requests` VALUES (1,'2024-05-22',1,'Не включается компьютер',1,1,'Завершена','Требуется замена блока питания','2024-05-22 13:46:35','19 часов'),(2,'2024-05-22',1,'Не включается',1,1,'Завершена','Требуется замена блока питания','2024-05-22 15:21:44','18 часов'),(3,'2024-05-23',8,'Не включается ноутбук.',6,2,'Новая заявка','null','2024-05-23 10:49:55',NULL),(4,'2024-05-23',9,'Тихий звук динамиков.',7,NULL,'Новая заявка',NULL,'2024-05-23 11:03:13',NULL);
/*!40000 ALTER TABLE `requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `username` varchar(16) NOT NULL,
  `password` varchar(32) NOT NULL,
  `role` enum('Администратор','Пользователь') NOT NULL DEFAULT 'Пользователь',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`username`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('admin','admin123','Администратор','2024-05-23 12:45:01'),('node','qwerty','Пользователь','2024-05-23 11:42:58');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workers`
--

DROP TABLE IF EXISTS `workers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workers` (
  `worker_id` int NOT NULL AUTO_INCREMENT,
  `worker_name` varchar(100) NOT NULL,
  `position` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`worker_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workers`
--

LOCK TABLES `workers` WRITE;
/*!40000 ALTER TABLE `workers` DISABLE KEYS */;
INSERT INTO `workers` VALUES (1,'Алексей Смирнов','Инженер'),(2,'Сергей Кузнецов','Техник'),(5,'Иван Иванов','Инженер'),(6,'Петр Петров','Техник'),(7,'Сергей Сергеев','Техник'),(8,'Алексей Алексеев','Техник');
/*!40000 ALTER TABLE `workers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-23 15:48:17
