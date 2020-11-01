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
  `Error_message` VARCHAR(45) NULL,
  PRIMARY KEY (`Error_ID`))
ENGINE = InnoDB;

INSERT INTO error(Error_ID, Error_message)
VALUES(1,'ERROR! insulin stopped');


-- -----------------------------------------------------
-- Table `mydb`.`patient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`patient` (
  `Patient_ID` INT NOT NULL,
  `Dose_1` INT NULL,
  `Dose_2` INT NULL,
  `Dose_3` INT NULL,
  `Error_ID` INT NOT NULL,
  PRIMARY KEY (`Patient_ID`),
  INDEX `fk_patient_error_idx` (`Error_ID` ASC) VISIBLE,
  CONSTRAINT `fk_patient_error`
    FOREIGN KEY (`Error_ID`)
    REFERENCES `mydb`.`error` (`Error_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

INSERT INTO patient(Patient_ID, Dose_1, Dose_2, Dose_3, Error_ID)
VALUES(1, 1, 2, 3, 1);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
