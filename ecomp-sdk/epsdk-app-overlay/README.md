# ECOMP SDK Web Application Overlay Files

## Overlay Project

This is a Maven project with the ECOMP SDK web application overlay. This 
project has common CSS, JavaScript, JSP and HTML files that are used in 
the AT&T internal version and the open-source version of the ECOMP SDK 
sample web application.  Those specific versions are built using separate 
Maven projects that copy in ("overlay") the contents of this project at 
package time. This is not a stand-alone web application.  

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

* Put new entries here *

Version 1.3.0, 28 August 2017
- Portal-17 removing eye.js and utils.js - rework
- Portal-19 Renaming the Group Id in the POM file to org.onap.portal.sdk
- Portal-21 Increased Role name size in UI  
- Portal-81 Add ONAP logo to header in app overlay
- PORTAL-86 Remove internal company URLs

Version 1.1.0
- Portal-7 Improvements added as part of the rebasing process
- Portal-10 Enhancing Drill Down Capabilities of EcompSDK Analytics
- PORTAL-17 Remove jfree related items

Version 1.0.0
- Initial release
