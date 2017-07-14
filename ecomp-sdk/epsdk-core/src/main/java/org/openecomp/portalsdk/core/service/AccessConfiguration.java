package org.openecomp.portalsdk.core.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessConfiguration {
	   
	
	
		/**
		 * 
		 * @returns RoleServiceImpl bean if  LocalAccessCondition is true
		 */
	   @Bean
	   @Conditional(LocalAccessCondition.class)
	   public RoleService roleServiceImpl() {
	      return  new RoleServiceImpl();
	   }
	   
	   
	   /**
		 * 
		 * @returns RoleServiceCentralizedAccess bean if  CentralAccessCondition is true
		 */
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public RoleService roleServiceCentralizedAccess() {
	      return  new RoleServiceCentralizedAccess();
	   }
	   
	   
	   /**
		 * 
		 * @returns LoginServiceImpl bean if  LocalAccessCondition is true
		 */
	   @Bean
	   @Conditional(LocalAccessCondition.class)
	   public LoginService loginServiceImpl() {
	      return  new LoginServiceImpl();
	   }
	   
	   
	   /**
		 * 
		 * @returns LoginServiceCentralizedImpl bean if  CentralAccessCondition is true
		 */
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public LoginService loginServiceCEntralizedImpl() {
	      return  new LoginServiceCentralizedImpl();
	   }
	   
	   /**
		 * 
		 * @returns UserProfileServiceImpl bean if  LocalAccessCondition is true
	     */
	   @Bean
	   @Conditional(LocalAccessCondition.class)
	   public UserService userServiceImpl() {
	      return  new UserServiceImpl();
	   }
	   
	   
	   /**
		 * 
		 * @returns returns UserProfileServiceCentalizedImpl bean if  CentralAccessCondition is true
	     */
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public UserService userServiceCentalizedImpl() {
	      return  new UserServiceCentalizedImpl();
	   }
	   
	   

	   /**
		 * 
		 * @returns returns ProfileServiceImpl bean if  LocalAccessCondition is true
	     */
	   @Bean
	   @Conditional(LocalAccessCondition.class)
	   public ProfileService profileServiceImpl() {
	      return  new ProfileServiceImpl();
	   }
	   
	   
	   /**
		 * 
		 * @returns returns ProfileServiceCentralizedImpl bean if  CentralAccessCondition is true
	     */
	   
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public ProfileService profileServiceCentralizedImpl() {
	      return  new ProfileServiceCentralizedImpl();
	   }
	   
	   /**
		 * 
		 * @returns returns UrlAccessCentalizedImpl bean if  CentralAccessCondition is true
	     */
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public UrlAccessService userUtilsCentalizedImpl() {
	      return  new UrlAccessCentalizedImpl();
	   }
	   
	   
	   /**
		 * 
		 * @returns returns UrlAccessImpl bean if  LocalAccessCondition is true
	     */
	   @Bean
	   @Conditional(LocalAccessCondition.class)
	   public UrlAccessService urlAccessImpl() {
	      return  new UrlAccessImpl();
	   }
	   
	   
	   /**
		 * 
		 * @returns returns RestApiRequestBuilder bean if  CentralAccessCondition is true
	     */
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public RestApiRequestBuilder restApiRequestBuilder() {
	      return  new RestApiRequestBuilder();
	   }
	  
}
