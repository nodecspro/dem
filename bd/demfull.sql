-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: 24m115
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
-- Table structure for table `Clients`
--

DROP TABLE IF EXISTS `Clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Clients` (
  `clientID` int NOT NULL AUTO_INCREMENT,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`clientID`),
  KEY `userID` (`userID`),
  CONSTRAINT `clients_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `Users` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Clients`
--

LOCK TABLES `Clients` WRITE;
/*!40000 ALTER TABLE `Clients` DISABLE KEYS */;
INSERT INTO `Clients` VALUES (1,1),(2,2),(3,3),(4,4),(5,5),(6,6),(7,7),(8,8),(9,9);
/*!40000 ALTER TABLE `Clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Comments`
--

DROP TABLE IF EXISTS `Comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Comments` (
  `commentID` int NOT NULL AUTO_INCREMENT,
  `message` varchar(255) DEFAULT NULL,
  `masterID` int DEFAULT NULL,
  `requestID` int DEFAULT NULL,
  PRIMARY KEY (`commentID`),
  KEY `masterID` (`masterID`),
  KEY `requestID` (`requestID`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`masterID`) REFERENCES `Masters` (`masterID`),
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`requestID`) REFERENCES `Requests` (`requestID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Comments`
--

LOCK TABLES `Comments` WRITE;
/*!40000 ALTER TABLE `Comments` DISABLE KEYS */;
INSERT INTO `Comments` VALUES (1,'Всё сделаем!',2,1),(2,'Не переживайте, починим.',3,2),(3,'Не переживайте, починим.',3,3);
/*!40000 ALTER TABLE `Comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Masters`
--

DROP TABLE IF EXISTS `Masters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Masters` (
  `masterID` int NOT NULL AUTO_INCREMENT,
  `userID` int DEFAULT NULL,
  PRIMARY KEY (`masterID`),
  KEY `userID` (`userID`),
  CONSTRAINT `masters_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `Users` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Masters`
--

LOCK TABLES `Masters` WRITE;
/*!40000 ALTER TABLE `Masters` DISABLE KEYS */;
INSERT INTO `Masters` VALUES (2,2),(3,3),(10,10);
/*!40000 ALTER TABLE `Masters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Requests`
--

DROP TABLE IF EXISTS `Requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Requests` (
  `requestID` int NOT NULL AUTO_INCREMENT,
  `startDate` date DEFAULT NULL,
  `computerTechType` varchar(100) DEFAULT NULL,
  `computerTechModel` varchar(100) DEFAULT NULL,
  `problemDescription` varchar(255) DEFAULT NULL,
  `requestStatus` varchar(50) DEFAULT 'Новая заявка',
  `completionDate` date DEFAULT NULL,
  `repairParts` varchar(255) DEFAULT NULL,
  `masterID` int DEFAULT NULL,
  `clientID` int DEFAULT NULL,
  PRIMARY KEY (`requestID`),
  KEY `masterID` (`masterID`),
  KEY `clientID` (`clientID`),
  CONSTRAINT `requests_ibfk_1` FOREIGN KEY (`masterID`) REFERENCES `Masters` (`masterID`),
  CONSTRAINT `requests_ibfk_2` FOREIGN KEY (`clientID`) REFERENCES `Clients` (`clientID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Requests`
--

LOCK TABLES `Requests` WRITE;
/*!40000 ALTER TABLE `Requests` DISABLE KEYS */;
INSERT INTO `Requests` VALUES (1,'2023-06-06','Компьютер','RDOR GAMING RAGE H290','Выключается после 10 минут работы','В процессе ремонта',NULL,NULL,2,7),(2,'2023-05-05','Ноутбук','ASUS VivoBook Pro 15 M6500QH-HN034 синий','Сильно шумит и греется','В процессе ремонта',NULL,NULL,3,8),(3,'2022-07-07','Мышка','ARDOR GAMING Phantom PRO','Перестало работать колёсико','Готова к выдаче','2023-01-01',NULL,3,9),(4,'2023-08-02','Клавиатура','Dark Project KD83A','Сломались некоторые клавиши','Новая заявка',NULL,NULL,NULL,8),(5,'2023-08-02','Ноутбук','ASUS ROG Strix G15 G513RW-HQ177 серый','Не загружается система','В процессе ремонта',NULL,'null',3,9),(8,'2024-05-24','sad','asdas','da','Новая заявка',NULL,'null',3,1);
/*!40000 ALTER TABLE `Requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Users` (
  `userID` int NOT NULL AUTO_INCREMENT,
  `fio` varchar(255) DEFAULT NULL,
  `phone` varchar(11) DEFAULT NULL,
  `login` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES (1,'Минаева Алиса Тимофеевна','89210563128','login1','pass1','Менеджер'),(2,'Воробьев Фёдор Алексеевич','89535078985','login2','pass2','Техник'),(3,'Захарова Алёна Андреевна','89210673849','login3','pass3','Техник'),(4,'Гусева Василиса Дмитриевна','89990563748','login4','pass4','Оператор'),(5,'Миронов Даниэль Львович','89994563847','login5','pass5','Оператор'),(6,'Белов Роман Добрынич','89219567849','login6','pass6','Заказчик'),(7,'Кузин Михаил Родионович','89219567841','login7','pass7','Заказчик'),(8,'Ковалева Софья Владимировна','89219567842','login8','pass8','Заказчик'),(9,'Глухова Вероника Владимировна','89219567843','login9','pass9','Заказчик'),(10,'Князев Арсений Андреевич','89219567844','login10','pass10','Техник');
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-24 17:53:02
