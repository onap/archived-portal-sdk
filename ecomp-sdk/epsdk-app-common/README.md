# ECOMP SDK Web Application Common Files

## Overview

This is a Maven project with the ECOMP SDK classes that are used in the open-source version of the ECOMP SDK sample web 
application.  Those specific versions are built using separate Maven projects. 

The java code depends on the following ECOMP SDK libraries that are developed
by the ECOMP Portal team:

* ECOMP SDK Framework library
* ECOMP SDK Core library
* ECOMP SDK Analytics library
* ECOMP SDK Workflow library

Use Apache Maven to build, package and deploy this webapp to a Maven repository.
This project is not intended to be deployed to a web container like Tomcat.

## Release Notes

SDK is currently using AngularJS version 1.4.8 for compatibility with b2b library 
version 1.0.1.  Note that previous versions of the EPSDK web application used  
AngularJS version 1.5.0.

### ONAP Distributions

Version 1.4.0
- PORTAL-19 Rename Java package base to org.onap
- PORTAL-42 Use OParent as parent POM
- PORTAL-72 Address Sonar Scan code issues
- PORTAL-90 Use approved ONAP license text
- Portal-86 Remove application specific usages from tests and other files


Version 1.3.0, 28 August 2017
- Portal-19 Renaming the Group Id in the POM file to org.onap.portal.sdk

Version 1.1.0
- Portal-7 Improvements added as part of the rebasing process
 
Version 1.0.0
- Initial release
