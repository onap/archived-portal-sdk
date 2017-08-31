# ONAP Portal SDK Framework

## Overview

This is the Maven project for the ONAP Portal SDK Framework library,
which is distributed as epsdk-fw-N.N.N.jar.  This library 
(once called third-party onboarding) provides features to 
partner applications that use a J2EE Servlet 3.0 container
as Apache Tomcat.  These features include:
- REST endpoint for use by the ECOMP Portal aplication. This endpoint 
  answers queries about roles, users and user-role assignments. 
  The endpoint methods are defined by the Java interface class 
  IPortalRestAPIService. Application developers must provide a 
  class that implements this interface, and publish the name of 
  that class in the properties file as discussed below.
- A session listener that updates a collection with current user sessions 
  as sessions are created and destroyed. This information is used to maintain 
  and extend user session timeouts across applications that are on-boarded to 
  the ECOMP portal.
- Single sign-on to the application via either the AT&T Central Security Platform (SCP) 
  or WebSEAL Junction.
- Communication with the ECOMP Portal to fetch a user-specific functional menu, either
  via REST or UEB.
  
Unlike the other ECOMP SDK libraries, this library does NOT require Hibernate, 
nor does it require Spring.

## Release Notes

### ONAP Distributions

Version 1.4.0
- PORTAL-19 Rename Java package base to org.onap
- PORTAL-42 Use OParent as parent POM
- PORTAL-72 Address Sonar Scan code issues
- PORTAL-90 Use approved ONAP license text
* Put new entries here *

Version 1.3.0, 28 August 2017
- PORTAL-19 Renaming the Group Id in the POM file to org.onap.portal.sdk
- PORTAL-64 Single sign-on from Portal fails for some applications

Version 1.1.0
- PORTAL-7 Improvements added as part of the rebasing process
 
Version 1.0.0
- Initial release
