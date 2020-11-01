-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`error`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`error` (
  `Error_ID` INT NOT NULL,
  `Error_message` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`Error_ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

INSERT INTO error(Error_ID, Error_message)
VALUES(1,' ');

-- -----------------------------------------------------
-- Table `mydb`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`user` (
  `ID` INT NOT NULL,
  `Cumulative_Dose` INT NULL DEFAULT NULL,
  `Error_ID` INT NULL,
  PRIMARY KEY (`ID`),
  INDEX `fk_patient_error_idx` (`Error_ID` ASC) VISIBLE,
  CONSTRAINT `fk_patient_error`
    FOREIGN KEY (`Error_ID`)
    REFERENCES `mydb`.`error` (`Error_ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

INSERT INTO user(ID, Cumulative_Dose, Error_ID)
VALUES(1, 0, 1);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
