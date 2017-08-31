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
package org.onap.portalsdk.core.conf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.onap.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableTransactionManagement
public class HibernateConfiguration {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(HibernateConfiguration.class);

	@Autowired
	private HibernateMappingLocatable hbMappingLocatable;

	@Autowired
	private DataSource dataSource;

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan(hbMappingLocatable.getPackagesToScan());
		sessionFactory.setHibernateProperties(getHibernateProperties());
		sessionFactory.setMappingLocations(hbMappingLocatable.getMappingLocations());
		return sessionFactory;
	}

	/**
	 * Builds a properties object with Hibernate properties in te system.properties file.
	 * 
	 * @return Properties object
	 */
	private Properties getHibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", SystemProperties.getProperty(SystemProperties.HB_DIALECT));
		properties.put("hibernate.show_sql", SystemProperties.getProperty(SystemProperties.HB_SHOW_SQL));
		return properties;
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public Map dataSourceMap() throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, ComboPooledDataSource> dataSourceMap = new HashMap<String, ComboPooledDataSource>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT schema_id,datasource_type,connection_url,user_name,password,driver_class,min_pool_size,max_pool_size,idle_connection_test_period FROM schema_info";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ComboPooledDataSource dataSource = new ComboPooledDataSource();
				dataSource.setDriverClass(rs.getString("driver_class"));
				dataSource.setJdbcUrl(rs.getString("connection_url"));
				dataSource.setUser(rs.getString("user_name"));
				dataSource.setPassword(rs.getString("password"));
				dataSource.setMinPoolSize(rs.getInt("min_pool_size"));
				dataSource.setMaxPoolSize(rs.getInt("max_pool_size"));
				dataSource.setIdleConnectionTestPeriod(rs.getInt("idle_connection_test_period"));
				dataSourceMap.put(rs.getString("schema_id"), dataSource);
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			conn.close();
			conn = null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger,
					"Error initializing database, verify database settings in properties file: " + e.getMessage(),
					AlarmSeverityEnum.CRITICAL);
			e.printStackTrace();
			dataSourceMap = null;
			throw e;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException se2) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

		return dataSourceMap;
	}

	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory s) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(s);
		return txManager;
	}

}
