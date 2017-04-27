This Readme file contains a description of all the database scripts located in  

  epsdk-app-common / db-scripts / 
  
***************************************************************************************************************************************

Directions: 

DDL
EcompSdkDDLMySql_1707_Common.sql  - this is the DDL entries that are in common
 
DML
EcompSdkDMLMySql_1707_Common.sql  - this is the DML entries that are in common


***************************************************************************************************************************************
  epsdk-app-common / db-scripts / 
***************************************************************************************************************************************	
5.EcompSdkDDLMySql_1707_Common.sql   		This script creates tables in the 1707 COMMON version of the ECOMP SDK application database.
											Additional DDL scripts may be required for different versions!
6.EcompSdkDMLMySql_1707_Common.sql   		This script populates tables in the 1707 COMMON version of the ECOMP SDK application database.
											Additional DML scripts are required for different versions!


*****************************************************
Upgrading from 1607 SDK to 1610 SDK
*****************************************************
EcompSdkDDL_1610_Add.sql					This is the Upgrade script for the 1610 Version of the SDK database called ecomp_sdk; 
											upgrading from the 1607 version
EcompSdkDML_1610_Add.sql					This is the Upgrade script for the default data for the 1610 Version of the SDK database called ecomp_sdk; 
											upgrading from the 1607 version
*****************************************************
Upgrading from 1610 SDK to 1702 SDK
*****************************************************	
If you are starting with a 1610 environment and want to bring it up to 1702,
you can run the following scripts in this order:
EcompSdkDML_1702_Add_1.sql
EcompSdkDML_1702_Add_2.sql
EcompSdkDML_1702_Add_3.sql
EcompSdkDML_1702_Add_4.sql
EcompSdkDML_1702_Add_5.sql

You can roll back the changes from the corresponding 1702 Add scripts with these rollback scripts:
EcompSdkDML_1702_Rollback_1.sql
EcompSdkDML_1702_Rollback_2.sql
EcompSdkDML_1702_Rollback_3.sql
EcompSdkDML_1702_Rollback_4.sql
EcompSdkDML_1702_Rollback_5.sql	
									
*****************************************************
Complete Scripts for 1702
*****************************************************											
EcompSdkDDLMySql_1702.sql					This is for the 1702 DDL Version of SDK database called ecomp_sdk
EcompSdkDMLMySql_1702.sql					This is for the default data for 1702 Version of SDK database called ecomp_sdk
