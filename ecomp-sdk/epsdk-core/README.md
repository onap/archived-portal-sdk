# ECOMP Portal SDK Core

## Overview

This is the Maven project for the ECOMP Portal SDK Core library,
which is distributed as epsdk-core-N.N.N.jar.  This library 
requires Hibernate and Spring, and provides many features 
such as data access, session management, logging, on-boarding 
and more.  Most of these features are demonstrated in the
ECOMP SDK web application. 

## Release Notes

### OpenECOMP Distributions

Build 1.2.8, ?? ??? 2017
* put new entries here *

Build 1.2.7, 10 Apr 2017
- Moved all annotated controllers to epsdk-common from epsdk-core
- Update shared context service for revised Portal endpoint path; drop separate property

Build 1.2.6, 23 Mar 2017
- DE273039 Adjust MenuListController to get OrgUserID from session, not cookie

Build 1.2.5, 16 Mar 2017
- No changes

Build 1.2.4, 10 Mar 2017
- No changes

Build 1.2.3, 8 Mar 2017
- No changes

Build 1.2.2, 6 Mar 2017
- US872039 Revise Element Map feature to drop absolute filesystem path in property file

Build 1.2.1, 2 Mar 2017
- US845636 Extend user-import controller to detect and return message on failure
- Move UEB/Cambria library and demonstration use classes into core (from FW)
- Exclude all log4j dependencies in core pom file 
- Remove references to ATTUID in UserUtils.java comments
- Remove logback.xml from src/main/resources
- Extend HibernateMappingLocatable and HibernateConfiguration to allow config of packages to scan
- Extend MenuListController.java to send content-type application/json 

Build 1.2.0, 9 Feb 2017
- Change group to org.openecomp.ecompsdkos; restart version numbering for open-source distribution

### Closed-Source Distributions

The following history is preserved for the benefit of partner application teams.

Version 4.3.5, 2 Feb 2017
- DE260606 Extend role controller to check existing roles when creating a new one

Version 4.3.4 13 Jan 2017
- No changes

Version 4.3.3, 11 Jan 2017
- DE239065 Adjust fix that initializes the start & stop timestamp on first call to loggers

Version 4.3.2, 9 Jan 2017
- DE261061 Remove System.out.println() debug output statements

Version 4.3.1, 3 Jan 2017
- DE239065 Initialize the start & stop timestamp on first call to the audit and metrics loggers

Version 4.2.1, 15 Dec 2016
- DE255409 trim trailing space on values fetched from Portal, System properties
- DE257028 add a constant value and modify the hibernate mapping to AuditLog class

Version 4.1.4, 22 Nov 2016
- DE250794 add trace-level logging methods to EELFLoggerDelegate
- US811188 add constants to AuditLog class

Version 4.1.3, 14 Nov 2016
- DE250319 All controllers that require user info should be Restricted within session
- Restore exclusion in pom to avoid pulling in outdated HttpServlet and other jars,
   which cause compile errors in SDK-App depending on Maven repository contents.

Version 4.1.2, 14 Nov 2016
- US777777 enhance network map mime type in ElementModelController

Version 4.1.1, 3 Nov 2016
- No changes.

Version 3.3.3, 13 Oct 2016
- DE240192 show useful message if cache configuration file is missing
- DE238612 new property app\_base\_url for apps using WebJunction address 
 
Version 3.3.2, 26 Sep 2016
- US710856 remove stray System.out.println statement and minor logging improvements. 
 
Version 3.3.1, 22 Sep 2016
- DE224872 fix errors shown in browser on profile page
- Show Quantum license information at top-right of screen
- Moved R Notebook controllers to SDK-Workflow project
- US710856 updated aspect-oriented programming (AOP) logging support,
   including the EELFLoggerDelegate and other classes.
- Updated favorites, functional menu, menu list, and profile controllers.
- Updated EELF Library to 0.0.5, the latest available version.
- US772823 Introduce Audit Log
- Remove some JFree code as open-source prep
- DE224872 fix errors shown in browser on profile page
- DE216279 fix behavior of pagination on profile page

Version 3.2.2, 4 Aug 2016
- New feature: R Cloud integration via a guard notebook
- New feature: fetch functional menu via REST
- Remove Spring annotation from OnBoardingApiServiceImpl class
- Extend EELFLoggerDelegate with methods that accept a throwable
- Revise logger to remove class name from MDC after logging 
- DE215237, fix script error on menu admin page
- DE214174, refresh menu contents after edit
- Moved MockApplicationContextTestSuite class into core, out of sdk-app
- Support application name at top of left menu
- DE210771, fix the multiple/invalid role assignment behavior
- New class for the error message returned as JSON
- Return error as JSON in case of unauthenticated request
- Removed database creation scripts used by ECOMP Portal
- Improved the shared context feature
 
Version 3.2.1, 12 Jul 2016
 - Use EELF loggers to be compliant with ECOMP project guidelines
 - Log controller requests to the audit log
 - Add alarm codes to the error log
 - Correct popup issue in profile page
 - Change the functional menu to show only user's first name
 - Show 20 items by default in the functional menu 
 - Refresh the left menu after items are edited 
 - Correct problem that prevented deletion of a role
 - Correct problem of missing country on Webphone import
 - US693240, support link-only onboarding
 - Extend logging to use instance_uuid from properties file
 - Show favorites menu items
 - Include logging ApplicationCodes.properties file in jar
 - Use EELFLoggingDelegate to ensure class names are shown
 - DE205174, correct problems in role functions
 - Remove Apache commons logging
 - Create new users as active (not inactive)
 - Move database scripts for SDK-App out of core, into app
 - Adjust OnBoardingApiServiceImpl to return null if user not found (for Portal)
 - Fix ASE editor position
 - Correct validation of menu entries

Version 3.1.1, 15 Jun 2016
 - Add custom logging filter to reduce UEB logging quantity
 - Fix admin/menu popup behavior
 - Correct hibernate mapping for user class and table
 
Version 2.2.0, 14 Jun 2016
 - Analytics and Workflow factored out of SDK-core library
 - Add application logout feature
 - Enhancements for DROOLS
 - Move DROOLs files to core and add library to POM
 - Revise logging for compliance with ECOMP guidelines
 - Revise error responses from REST interface to be JSON
 - Adjust HTML template for profile page
 - Rename war directory to src/main/webapp
 - Show error information if user is not found
 - Split database scripts into Portal/SDK versions
 - Add client for new shared context feature in Portal
 - Revise favorites implementation to use app-to-app REST comms
 - Exclude commons-logging and log4j libraries in POM
 - Remove try/catch/discard block for exception in DataAccessServiceImpl
 

