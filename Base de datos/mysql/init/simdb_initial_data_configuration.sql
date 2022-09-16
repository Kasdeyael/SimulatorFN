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
-- Table structure for table `initial_data_configuration`
--

DROP TABLE IF EXISTS `initial_data_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `initial_data_configuration` (
  `configurationUID` int unsigned NOT NULL,
  `netType` tinyint unsigned NOT NULL,
  `nUsers` smallint unsigned NOT NULL,
  `linksPerNode` smallint unsigned DEFAULT NULL,
  `networkSeed` mediumint unsigned NOT NULL,
  `nInfected` smallint unsigned NOT NULL,
  `initialNodesNetwork` smallint unsigned DEFAULT NULL,
  `probConnect` decimal(9,8) unsigned DEFAULT NULL,
  PRIMARY KEY (`configurationUID`),
  CONSTRAINT `fk_initial_main` FOREIGN KEY (`configurationUID`) REFERENCES `main_configuration` (`configurationUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table for the initial data per configuration, stating all the probabilities and number of users for any model.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `initial_data_configuration`
--

LOCK TABLES `initial_data_configuration` WRITE;
/*!40000 ALTER TABLE `initial_data_configuration` DISABLE KEYS */;
INSERT INTO `initial_data_configuration` VALUES (1,1,1000,10,17,2,10,NULL),(2,1,1000,10,17,2,10,NULL),(3,1,1000,10,17,2,10,NULL),(4,1,1000,10,17,2,10,NULL),(5,1,1000,10,17,2,10,NULL),(6,1,1000,10,17,2,10,NULL);
/*!40000 ALTER TABLE `initial_data_configuration` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-08-01 11:51:05
