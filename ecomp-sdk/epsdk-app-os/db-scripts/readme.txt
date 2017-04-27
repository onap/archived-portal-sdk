This Readme file contains a description of open source scripts located in  

  epsdk-app-os     / db-scripts / 
  
***************************************************************************************************************************************

Directions: 

DDL
For an Opensource instance run only script EcompSdkDDLMySql_1707_Common.sql;

EcompSdkDDLMySql_1707_Common.sql  - common DDL entries
EcompSdkDDLMySql_1707_OS.sql is only a placeholder at this time.

DML

For an Opensource instance run  script EcompSdkDMLMySql_1707_Common.sql and script EcompSdkDMLMySql_1707_OS.sql; 

EcompSdkDMLMySql_1707_Common.sql  - common DML entries
EcompSdkDMLMySql_1707_OS.sql - DML entries for Opensource needs
	
To Upgrade the OpenSource 1610.2 version to the 1707 version, run script EcompSdkMySql_Upgrade_1610.2_to_1707_OS.sql
and to remove those changes run EcompSdkMySql_Rollback_1707_to_1610.2_OS.sql.

EcompSdkMySql_Upgrade_1610.2_to_1707_OS.sql
EcompSdkMySql_Rollback_1707_to_1610.2_OS.sql


***************************************************************************************************************************************
epsdk-app-os / db-scripts / 
***************************************************************************************************************************************	
EcompSdkDDLMySql_1707_OS.sql  			It is empty for now; just a logical placeholder
EcompSdkDMLMySql_1707_OS.sql  			This script populates tables in the 1707 OPEN-SOURCE version of the ECOMP SDK application database.
											After The DML 1707 COMMON script is run.
EcompSdkMySql_Upgrade_1610.2_to_1707_OS.sql     This script upgrades the ECOMP SDK App database from version 1610.2 to 1707.
EcompSdkMySql_Rollback_1707_to_1610.2_OS.sql   This script rolls-back the upgrade for the ECOMP SDK App database 
