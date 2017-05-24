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

### OpenECOMP Distributions

Version 1.1.0
[Portal-7] Improvements added as part of the rebasing process
[Portal-10] Enhancing Drill Down Capabilities of EcompSDK Analytics

Version 1.0.0
- Initial release
