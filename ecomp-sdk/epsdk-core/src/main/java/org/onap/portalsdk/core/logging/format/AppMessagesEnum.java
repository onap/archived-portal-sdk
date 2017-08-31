/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalsdk.core.logging.format;

public enum AppMessagesEnum {
	/*
	100-199 Security/Permission Related 
										- Authentication problems (from external client, to external server)
										- Certification errors
										- 
											
	200-299 Availability/Timeout Related
										- connectivity error
										- connection timeout
										
	300-399 Data Access/Integrity Related
										- Data in graph in invalid(E.g. no creator is found for service) 
										- Artifact is missing in ES, but exists in graph.
										 
	400-499 Schema Interface Type/Validation
										- received Pay-load checksum is invalid
										- received JSON is not valid
							  
	500-599 Business/Flow Processing Related  
										-  check out to service is not allowed
										-  Roll-back is done
										-  failed to generate heat file
										  

	600-899 Reserved - do not use

	900-999 Unknown Errors 
										- Unexpected exception
										*/
	
	BeUebAuthenticationError(ErrorCodesEnum.BEUEBAUTHENTICATIONERROR_ONE_ARGUMENT, ErrorTypeEnum.AUTHENTICATION_PROBLEM, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
								"ERR100E", "An Authentication failure occurred during access to UEB server", "Details: {0}.", "Please check UEB server list and keys configured under Portal.Properties file."),
	
	BeRestApiAuthenticationError(ErrorCodesEnum.BERESTAPIAUTHENTICATIONERROR, ErrorTypeEnum.AUTHENTICATION_PROBLEM, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
								"ERR101E", "Rejected an incoming REST API request due to invalid credentials", "", "Please check application credentials defined in Database or properties files."),
	
	InternalAuthenticationInfo(ErrorCodesEnum.INTERNALAUTHENTICATIONINFO_ONE_ARGUMENT, ErrorTypeEnum.AUTHENTICATION_PROBLEM, AlarmSeverityEnum.INFORMATIONAL, ErrorSeverityEnum.INFO,
								"ERR199I", "Internal authentication problem", "Details: {0}.", "Please check the logs for more information."),
	
	InternalAuthenticationWarning(ErrorCodesEnum.INTERNALAUTHENTICATIONWARNING_ONE_ARGUMENT, ErrorTypeEnum.AUTHENTICATION_PROBLEM, AlarmSeverityEnum.MINOR, ErrorSeverityEnum.WARN,
								"ERR199W", "Internal authentication problem", "Details: {0}.", "Please check the logs for more information."),
	
	InternalAuthenticationError(ErrorCodesEnum.INTERNALAUTHENTICATIONERROR_ONE_ARGUMENT, ErrorTypeEnum.AUTHENTICATION_PROBLEM, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
								"ERR199E", "Internal authentication problem", "Details: {0}.", "Please check the logs for more information."),
	
	InternalAuthenticationFatal(ErrorCodesEnum.INTERNALAUTHENTICATIONFATAL_ONE_ARGUMENT, ErrorTypeEnum.AUTHENTICATION_PROBLEM, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.FATAL,
								"ERR199F", "Internal authentication problem", "Details: {0}.", "Please check the logs for more information."),
	
	BeHealthCheckError(ErrorCodesEnum.BeHEALTHCHECKERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.ERROR,
								"ERR200E", "ECOMP-PORTAL Back-end probably lost connectivity to either one of the following components: MySQL DB, UEB Cluster", "", "Please check the logs for more information."),

	BeHealthCheckMySqlError(ErrorCodesEnum.BEHEALTHCHECKMYSQLERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.ERROR,
								"ERR201E", "ECOMP-PORTAL Back-end probably lost connectivity to MySQL DB", "", "Check connectivity to MYSQL is configured correctly under system.properties file."),

	BeHealthCheckUebClusterError(ErrorCodesEnum.BEHEALTHCHECKUEBCLUSTERERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.ERROR,
								"ERR203E", "ECOMP-PORTAL Back-end probably lost connectivity to UEB Cluster", "", "Check connectivity to UEB cluster which is configured under portal.properties file."),
	
	FeHealthCheckError(ErrorCodesEnum.FEHEALTHCHECKERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.ERROR,
								"ERR204E", "Unable to connect to a valid ECOMP-PORTAL Back-end Server.", "", "Please check connectivity from this FE instance towards BE or BE Load Balancer."),
	
	BeHealthCheckRecovery(ErrorCodesEnum.BEHEALTHCHECKRECOVERY, ErrorTypeEnum.RECOVERY, AlarmSeverityEnum.INFORMATIONAL, ErrorSeverityEnum.INFO,
								"ERR205I", "ECOMP-PORTAL Back-end Recovery to either one of the following components: MySQL DB, UEB Cluster", "", "Please check logs for more specific information about the problem."),
	
	BeHealthCheckMySqlRecovery(ErrorCodesEnum.BEHEALTHCHECKMYSQLRECOVERY, ErrorTypeEnum.RECOVERY, AlarmSeverityEnum.INFORMATIONAL, ErrorSeverityEnum.INFO,
								"ERR206I", "ECOMP-PORTAL Back-end connection recovery to MySQL DB", "", "Please check logs for more specific information about the problem."),
	
	BeHealthCheckUebClusterRecovery(ErrorCodesEnum.BEHEALTHCHECKUEBCLUSTERRECOVERY, ErrorTypeEnum.RECOVERY, AlarmSeverityEnum.INFORMATIONAL, ErrorSeverityEnum.INFO,
								"ERR208I", "ECOMP-PORTAL Back-end connection recovery to UEB Cluster", "", "Please check logs for more specific information about the problem."),
	
	FeHealthCheckRecovery(ErrorCodesEnum.FEHEALTHCHECKRECOVERY, ErrorTypeEnum.RECOVERY, AlarmSeverityEnum.INFORMATIONAL, ErrorSeverityEnum.INFO,
								"ERR209I", "Connectivity to ECOMP-PORTAL Front-end Server is recovered", "", "Please check logs for more specific information about the problem."),
    
	BeUebConnectionError(ErrorCodesEnum.BEUEBCONNECTIONERROR_ONE_ARGUMENT, ErrorTypeEnum.CONNECTION_PROBLEM, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR210E", "ECOMP-PORTAL Back-end probably lost connectivity to UEB Cluster", "Details: {0}.", "Please check UEB server list and keys configured under Portal.Properties file."),
    
    BeUebUnkownHostError(ErrorCodesEnum.BEUEBUNKOWNHOSTERROR_ONE_ARGUMENT, ErrorTypeEnum.CONNECTION_PROBLEM, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR211E", "ECOMP-PORTAL Back-end probably lost connectivity to UEB Cluster", "Cannot reach host: {0}.", "Please check UEB server list and keys configured under Portal.Properties file."),
	
    BeUebRegisterOnboardingAppError(ErrorCodesEnum.BEUEBREGISTERONBOARDINGAPPERROR, ErrorTypeEnum.CONNECTION_PROBLEM, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR212E", "Failed to register the On-boarding application with UEB Communication server", "Details: {0}.", "Please check UEB server list and keys configured under Portal.Properties file."),
    
    BeHttpConnectionError(ErrorCodesEnum.BEHTTPCONNECTIONERROR_ONE_ARGUMENT, ErrorTypeEnum.CONNECTION_PROBLEM, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
								"ERR213E", "It could be that communication to an external application might resulted an exception or failed to reach the external application", 
								"Details: {0}.", "Please check logs for more information."),
    
    InternalConnectionInfo(ErrorCodesEnum.INTERNALCONNECTIONINFO_ONE_ARGUMENT, ErrorTypeEnum.CONNECTION_PROBLEM, AlarmSeverityEnum.INFORMATIONAL, ErrorSeverityEnum.INFO,
								"ERR299I", "Internal Connection problem", "Details: {0}.", "Please check logs for more information."),
    
	InternalConnectionWarning(ErrorCodesEnum.INTERNALCONNECTIONWARNING_ONE_ARGUMENT, ErrorTypeEnum.CONNECTION_PROBLEM, AlarmSeverityEnum.MINOR, ErrorSeverityEnum.WARN,
								"ERR299W", "Internal Connection problem", "Details: {0}.", "Please check logs for more information."),
    
    InternalConnectionError(ErrorCodesEnum.INTERNALCONNECTIONERROR_ONE_ARGUMENT, ErrorTypeEnum.CONNECTION_PROBLEM, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR299E", "Internal Connection problem", "Details: {0}.", "Please check logs for more information."),
    
    InternalConnectionFatal(ErrorCodesEnum.INTERNALCONNECTIONFATAL_ONE_ARGUMENT, ErrorTypeEnum.CONNECTION_PROBLEM, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.FATAL,
    							"ERR299F", "Internal Connection problem", "Details: {0}.", "Please check logs for more information."),
    
    BeUebObjectNotFoundError(ErrorCodesEnum.BEUEBOBJECTNOTFOUNDERROR_ONE_ARGUMENT, ErrorTypeEnum.DATA_ERROR, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR303E", "Error occurred during access to U-EB Server.", "Data not found: {0}.", "An error occurred during access to UEB Server, {1} failed to either register or unregister to/from UEB topic."),
    
    BeUserMissingError(ErrorCodesEnum.BEUSERMISSINGERROR_ONE_ARGUMENT, ErrorTypeEnum.DATA_ERROR, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR310E", "User is not found", "", "User {0} must be added to the corresponding application with proper user roles."),
    
    BeUserInactiveWarning(ErrorCodesEnum.BEUSERINACTIVEWARNING_ONE_ARGUMENT, ErrorTypeEnum.DATA_ERROR, AlarmSeverityEnum.MINOR, ErrorSeverityEnum.WARN,
    							"ERR313W", "User is found but in-active", "", "User {0} must be added to the corresponding application with proper user roles."),
    
    BeUserAdminPrivilegesInfo(ErrorCodesEnum.BEUSERADMINPRIVILEGESINFO_ONE_ARGUMENT, ErrorTypeEnum.DATA_ERROR, AlarmSeverityEnum.MINOR, ErrorSeverityEnum.WARN,
    							"ERR314W", "User is found but don't have administrative privileges", "", "User {0} should be given administrator role for the corresponding application to perform the necessary actions."),
    
    BeInvalidJsonInput(ErrorCodesEnum.BEINVALIDJSONINPUT, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR405E", "Failed to convert JSON input to object", "", "Please check logs for more information."),
    
    BeIncorrectHttpStatusError(ErrorCodesEnum.BEINCORRECTHTTPSTATUSERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR407E", "Communication to an external application is resulted in with Incorrect Http response code", "", "Please check logs for more information."),
    
    BeInitializationError(ErrorCodesEnum.BEINITIALIZATIONERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.ERROR,
    							"ERR500E", "ECOMP-PORTAL Back-end was not initialized properly", "", "Please check logs for more information."),
    
    BeUebSystemError(ErrorCodesEnum.BEUEBSYSTEMERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR502E", "Error occurred during access to U-EB Server", "Details: {0}.", "An error occurred in {1} distribution mechanism. Please check the logs for more information."),
    
    BeDaoSystemError(ErrorCodesEnum.BEDAOSYSTEMERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.ERROR,
    							"ERR505E", "Performing DDL or DML operations on database might have failed", "", "Please check MySQL DB health or look at the logs for more details."),
    
    BeSystemError(ErrorCodesEnum.BESYSTEMERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.ERROR,
    							"ERR506E", "Unexpected error during operation", "", "Please check logs for more information."),
    
    BeExecuteRollbackError(ErrorCodesEnum.BEEXECUTEROLLBACKERROR, ErrorTypeEnum.DATA_ERROR, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR507E", "Roll-back operation towards database has failed", "", "Please check MYSQL DB health or look at the logs for more details."),
    
    FeHttpLoggingError(ErrorCodesEnum.FEHTTPLOGGINGERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.MINOR, ErrorSeverityEnum.ERROR,
    							"ERR517E", "Error when logging FE HTTP request/response", "", "Please check MYSQL DB health or look at the logs for more details."),
    
	FePortalServletError(ErrorCodesEnum.FEPORTALSERVLETERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
								"ERR518E", "Error when trying to access FE Portal page.", "", "Please check logs for more information."),
	
    BeDaoCloseSessionError(ErrorCodesEnum.BEDAOCLOSESESSIONERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR519E", "Close local session operation with database failed", "", "Please check MYSQL DB health or look at the logs form more details."),
    
    BeRestApiGeneralError(ErrorCodesEnum.BERESTAPIGENERALERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.ERROR,
    							"ERR900E", "Unexpected error during ECOMP-PORTAL Back-end REST API execution", "", "Please check error log for more information."),
    
    FeHealthCheckGeneralError(ErrorCodesEnum.FEHEALTHCHECKGENERALERROR, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.ERROR,
    							"ERR901E", "General error during FE Health Check", "", "Please check error log for more information."),
    
    InternalUnexpectedInfo(ErrorCodesEnum.INTERNALUNEXPECTEDINFO_ONE_ARGUMENT, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.INFORMATIONAL, ErrorSeverityEnum.INFO,
    							"ERR999I", "Unexpected error", "Details: {0}.", "Please check logs for more information."),
    
    InternalUnexpectedWarning(ErrorCodesEnum.INTERNALUNEXPECTEDWARNING_ONE_ARGUMENT, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.MINOR, ErrorSeverityEnum.WARN,
    							"ERR999W", "Unexpected error", "Details: {0}.", "Please check logs for more information."),
    
    InternalUnexpectedError(ErrorCodesEnum.INTERNALUNEXPECTEDERROR_ONE_ARGUMENT, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.MAJOR, ErrorSeverityEnum.ERROR,
    							"ERR999E", "Unexpected error", "Details: {0}.", "Please check logs for more information."),
    
    InternalUnexpectedFatal(ErrorCodesEnum.INTERNALUNEXPECTEDFATAL_ONE_ARGUMENT, ErrorTypeEnum.SYSTEM_ERROR, AlarmSeverityEnum.CRITICAL, ErrorSeverityEnum.FATAL,
    							"ERR999F", "Unexpected error", "Details: {0}.", "Please check logs for more information."),
	
	;
	
	ErrorTypeEnum eType;
	AlarmSeverityEnum alarmSeverity;
	ErrorCodesEnum messageCode;
	ErrorSeverityEnum errorSeverity;
	String errorCode;
	String errorDescription;
	String details;
	String resolution;
	
	AppMessagesEnum(ErrorCodesEnum messageCode, ErrorTypeEnum eType, AlarmSeverityEnum alarmSeverity, ErrorSeverityEnum errorSeverity, String errorCode, String errorDescription, 
						String details, String resolution) {
		this.messageCode = messageCode;
		this.eType = eType;
		this.alarmSeverity = alarmSeverity;
		this.errorSeverity = errorSeverity;
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.details = details;
		this.resolution = resolution;
	}
	
	public String getDetails() {
		return this.details;
	}
	
	public String getResolution() {
		return this.resolution;
	}
	public String getErrorCode() {
		return this.errorCode;
	}
	
	public String getErrorDescription() {
		return this.errorDescription;
	}
	
	public ErrorSeverityEnum getErrorSeverity() {
		return this.errorSeverity;
	}
	
	public void setErrorSeverity(ErrorSeverityEnum errorSeverity) {
		this.errorSeverity = errorSeverity;
	}
	
	public ErrorCodesEnum getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(ErrorCodesEnum messageCode) {
		this.messageCode = messageCode;
	}
	
	public AlarmSeverityEnum getAlarmSeverity() {
		return alarmSeverity;
	}

	public void setAlarmSeverity(AlarmSeverityEnum alarmSeverity) {
		this.alarmSeverity = alarmSeverity;
	}
	
	public ErrorTypeEnum getErrorType() {
		return eType;
	}

	public void setErrorType(ErrorTypeEnum eType) {
		this.eType = eType;
	}
}
