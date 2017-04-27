# ECOMP Portal SDK Framework

## Overview

This is the Maven project for the ECOMP Portal SDK Framework library,
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

### OpenECOMP Distributions

Build 1.2.8, ?? ??? 2017
* put new entries here *

Build 1.2.7, 10 Apr 2017
- No changes

Build 1.2.6, 23 Mar 2017
- No changes

Build 1.2.5, 16 Mar 2017
- Extended PortalTimeoutHandler to show method names in all exception output
- US869765 Post OpenSource SDKs and ecompFW apps should listen to endpoint /api/v2 instead of just /api

Build 1.2.4, 10 Mar 2017
- No changes

Build 1.2.3, 8 Mar 2017
- No changes

Build 1.2.2, 6 Mar 2017
- No changes

Build 1.2.1, 2 Mar 2017
- Refactor to remove dependency on AT&T Global Log On single sign on (SSO) library.
- Move UEB/Cambria library and demonstration use classes out of FW

Build 1.2.0, 9 Feb 2017
- Change group to org.openecomp.ecompsdkos; restart version numbering for open-source distribution

### Closed-Source Distributions

The following history is preserved for the benefit of partner application teams.

Version 4.3.5, 2 Feb 2017
- No changes

Version 4.3.4 13 Jan 2017
- No changes

Version 4.3.3, 11 Jan 2017
- US779882 Extend analytics servlet to use content type text/javascript for analytics script;
    check for empty user ID when validating analytics GET/POST requests 
 
Version 4.3.2, 9 Jan 2017 
- US779882 Extend servlet to provide GET/POST endpoints for Web Analytics 

Version 4.3.1, 3 Jan 2017 
- No changes

Version 4.2.1, 15 Dec 2016
- DE255409 trim trailing space on values fetched from Portal, System properties

Version 4.1.4, 22 Nov 2016
 - No changes

Version 4.1.3, 14 Nov 2016
 - No changes

Version 4.1.2, 14 Nov 2016
 - No changes

Version 4.1.1, 3 Nov 2016
 - No changes

Version 3.3.3, 13 Oct 2016
 - No changes

Version 3.3.2, 26 Sep 2016
 - DE237818, fix bug that blocked return of session timeout information 
 
Version 3.3.1, 22 Sep 2016
 - No changes

Version 3.2.2, 4 Aug 2016

Version 3.2.1, 12 July 2016

Version 2.0.3, 13 June 2016

Version 2.0.2, 27 May 2016
