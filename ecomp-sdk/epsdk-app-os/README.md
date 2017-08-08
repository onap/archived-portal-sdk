# ECOMP SDK Web Application for Open Source

## Overview

This is a Maven project with the ECOMP SDK web application for public release,
containing files specific to requirements of the open-source version.  This 
project uses the Maven war plugin to copy in ("overlay") the contents of the 
ECOMP SDK web application overlay files distribution at package time.

Use Apache Maven to build, package and deploy this webapp to a web container
like Apache Tomcat.  Eclipse users must install the M2E-WTP connector, see 
https://www.eclipse.org/m2e-wtp/

## Release Notes

Version 1.3.0
- PORTAL-17 removing eye.js and utils.js
- PORTAL-19 Renaming the Group Id in the POM file to org.onap.portal.sdk
- PORTAL-34 Restore required properties in fusion.properties file
- PORTAL-64 Single sign-on from Portal fails for some applications

Version 1.1.0
- PORTAL-6 Updates to License and Trademark in the PORTAL Source Code
- PORTAL-7 Improvements added as part of the rebasing process
- PORTAL 13 ecompsdk db connection intermittent issue seen for VID app 
- PORTAL 15 Fix Charting and Search Capabilities of EcompSDK Analytics
- PORTAL 23 Updating the SDK version from Snapshot to Release 1.1.0
 
Version 1.0.0
- Initial release
