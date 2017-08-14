This Readme file contains a description of open source scripts located in  

  epsdk-app-os     / db-scripts / 
  
***************************************************************************************************************************************

Directions: 

DDL
For ONAP instance run EcompSdkDDLMySql_1710_Common.sql add script EcompSdkDDLMySql_1710_OS.sql.

EcompSdkDDLMySql_1710_Common.sql  - this is the DDL entries that both Opensource and AT&T have in common
EcompSdkDDLMySql_1710_OS.sql -  this is the specific DDL entries that only OS needs

DML
For an ONAP instance run script EcompSdkDMLMySql_1710_Common.sql and script EcompSdkDMLMySql_1710_OS.sql.

EcompSdkDMLMySql_1707_Common.sql  - common DML entries 
EcompSdkDMLMySql_1707_OS.sql - DML entries for Opensource needs

Our Existing Partner Apps can call the following scripts to upgrade from earlier version

EcompSdkMySql_Upgrade_1707_to_1710_Common.sql
EcompSdkMySql_Rollback_1710_to_1707_Common.sql
