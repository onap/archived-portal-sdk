package org.openecomp.portalsdk.core.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessConfiguration {
	   
	
	
		/**
		 * 
		 * @return RoleServiceImpl bean if  LocalAccessCondition is true
		 */
	   @Bean
	   @Conditional(LocalAccessCondition.class)
	   public RoleService roleServiceImpl() {
	      return  new RoleServiceImpl();
	   }
	   
	   
	   /**
		 * 
		 * @return RoleServiceCentralizedAccess bean if  CentralAccessCondition is true
		 */
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public RoleService roleServiceCentralizedAccess() {
	      return  new RoleServiceCentralizedAccess();
	   }
	   
	   
	   /**
		 * 
		 * @return LoginServiceImpl bean if  LocalAccessCondition is true
		 */
	   @Bean
	   @Conditional(LocalAccessCondition.class)
	   public LoginService loginServiceImpl() {
	      return  new LoginServiceImpl();
	   }
	   
	   
	   /**
		 * 
		 * @return LoginServiceCentralizedImpl bean if  CentralAccessCondition is true
		 */
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public LoginService loginServiceCEntralizedImpl() {
	      return  new LoginServiceCentralizedImpl();
	   }
	   
	   /**
		 * 
		 * @return UserProfileServiceImpl bean if  LocalAccessCondition is true
	     */
	   @Bean
	   @Conditional(LocalAccessCondition.class)
	   public UserService userServiceImpl() {
	      return  new UserServiceImpl();
	   }
	   
	   
	   /**
		 * 
		 * @return returns UserProfileServiceCentalizedImpl bean if  CentralAccessCondition is true
	     */
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public UserService userServiceCentalizedImpl() {
	      return  new UserServiceCentalizedImpl();
	   }
	   
	   

	   /**
		 * 
		 * @return returns ProfileServiceImpl bean if  LocalAccessCondition is true
	     */
	   @Bean
	   @Conditional(LocalAccessCondition.class)
	   public ProfileService profileServiceImpl() {
	      return  new ProfileServiceImpl();
	   }
	   
	   
	   /**
		 * 
		 * @return returns ProfileServiceCentralizedImpl bean if  CentralAccessCondition is true
	     */
	   
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public ProfileService profileServiceCentralizedImpl() {
	      return  new ProfileServiceCentralizedImpl();
	   }
	   
	   
	   /**
		 * 
		 * @return returns RestApiRequestBuilder bean if  CentralAccessCondition is true
	     */
	   @Bean
	   @Conditional(CentralAccessCondition.class)
	   public RestApiRequestBuilder restApiRequestBuilder() {
	      return  new RestApiRequestBuilder();
	   }
	  
}
