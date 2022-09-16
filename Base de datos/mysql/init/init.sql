-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: simdb
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
-- Table structure for table `dataset_information`
--

DROP TABLE IF EXISTS `dataset_information`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dataset_information` (
  `datasetUID` tinyint unsigned NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `isDaily` tinyint unsigned NOT NULL,
  `isSmoothed` tinyint unsigned NOT NULL,
  PRIMARY KEY (`datasetUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Table for the information about each dataset, pertaining its representation and identification.';
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `main_configuration`
--

DROP TABLE IF EXISTS `main_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `main_configuration` (
  `configurationUID` int unsigned NOT NULL,
  `execution` smallint unsigned NOT NULL,
  `randomSeed` mediumint unsigned NOT NULL,
  PRIMARY KEY (`configurationUID`,`execution`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table for storing all the configurations and its executions.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `real_dataset_spread`
--

DROP TABLE IF EXISTS `real_dataset_spread`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `real_dataset_spread` (
  `datasetUID` tinyint unsigned NOT NULL,
  `timestamp` tinyint unsigned NOT NULL,
  `endorserLabel` decimal(11,8) unsigned DEFAULT NULL,
  `denierLabel` decimal(11,8) unsigned DEFAULT NULL,
  `totalSpreading` decimal(11,8) unsigned DEFAULT NULL,
  PRIMARY KEY (`datasetUID`,`timestamp`),
  CONSTRAINT `fk_dataset_spread` FOREIGN KEY (`datasetUID`) REFERENCES `dataset_information` (`datasetUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table for storing the dataset spread.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rmse_results`
--

DROP TABLE IF EXISTS `rmse_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rmse_results` (
  `configurationUID` int unsigned NOT NULL,
  `execution` smallint unsigned NOT NULL,
  `datasetUID` tinyint unsigned NOT NULL,
  `RMSE` decimal(11,8) unsigned NOT NULL,
  PRIMARY KEY (`configurationUID`,`execution`,`datasetUID`),
  KEY `datUD_idx` (`datasetUID`),
  CONSTRAINT `fk_rmse_dataset` FOREIGN KEY (`datasetUID`) REFERENCES `dataset_information` (`datasetUID`),
  CONSTRAINT `fk_rmse_main` FOREIGN KEY (`configurationUID`, `execution`) REFERENCES `main_configuration` (`configurationUID`, `execution`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table for storing the results of RMSE for each run and for a specific dataset.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `state_per_run`
--

DROP TABLE IF EXISTS `state_per_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `state_per_run` (
  `configurationUID` int unsigned NOT NULL,
  `execution` smallint unsigned NOT NULL,
  `tick` smallint unsigned NOT NULL,
  `vaccinated` smallint unsigned NOT NULL,
  `cured` smallint unsigned NOT NULL,
  `infected` smallint unsigned NOT NULL,
  `neutral` smallint unsigned NOT NULL,
  PRIMARY KEY (`configurationUID`,`execution`,`tick`),
  KEY `Execution_idx` (`execution`),
  CONSTRAINT `fk_state_main` FOREIGN KEY (`configurationUID`, `execution`) REFERENCES `main_configuration` (`configurationUID`, `execution`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table for storing the output state of the agents, for each run.';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-08-04 18:08:27
