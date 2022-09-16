-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: simdb
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `data_configuration_by_model`
--

DROP TABLE IF EXISTS `data_configuration_by_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `data_configuration_by_model` (
  `configurationUID` int unsigned NOT NULL,
  `modelType` tinyint unsigned NOT NULL,
  `probAcceptDeny` decimal(9,8) unsigned NOT NULL,
  `probInfect` decimal(9,8) unsigned NOT NULL,
  `probMakeDenier` decimal(9,8) unsigned NOT NULL,
  `nBots` smallint unsigned DEFAULT NULL,
  `nInfluencers` smallint unsigned DEFAULT NULL,
  `probInfl` decimal(9,8) unsigned DEFAULT NULL,
  `engagement` decimal(9,8) unsigned DEFAULT NULL,
  PRIMARY KEY (`configurationUID`),
  CONSTRAINT `fk_model_main` FOREIGN KEY (`configurationUID`) REFERENCES `main_configuration` (`configurationUID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table for the probabilities used on each configuration, relative to each model.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data_configuration_by_model`
--

LOCK TABLES `data_configuration_by_model` WRITE;
/*!40000 ALTER TABLE `data_configuration_by_model` DISABLE KEYS */;
INSERT INTO `data_configuration_by_model` VALUES (1,1,0.00440000,0.01100000,0.00440000,NULL,NULL,NULL,NULL),(2,2,0.00410000,0.01100000,0.00430000,2,5,0.02000000,NULL),(3,3,0.00450000,0.01070000,0.00470000,2,5,0.01900000,1.00000000),(4,4,0.00450000,0.01070000,0.00470000,2,5,0.01900000,1.00000000),(5,3,0.00460000,0.01070000,0.00440000,2,5,0.01900000,1.00000000),(6,3,0.00450000,0.01090000,0.00440000,2,5,0.01000000,0.99000000);
/*!40000 ALTER TABLE `data_configuration_by_model` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-08-01 11:51:04
