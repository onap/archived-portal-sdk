/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.analytics.system.fusion.adapter;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletContext;

import org.openecomp.portalsdk.core.FusionObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class FusionAdapter implements FusionObject {

    public static final String LOCAL_SESSION_FACTORY_KEY = "local";


    private ComboPooledDataSource dataSource;
    private Map<String,ComboPooledDataSource> dataSourceMap;
    
    //private 		SessionFactory sessionFactory;
    private        ServletContext     servletContext;
    
   // private static Connection connection = null;

    //private static AdapterSessionFactoryContainer sessionFactoryContainer;


    public FusionAdapter() {
    }


    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

   // public static AdapterSessionFactoryContainer getSessionFactoryContainer() {
     //   return sessionFactoryContainer;
   // }

    //public static LinkedHashMap getSessionFactories() {
      //  return getSessionFactoryContainer().getSessionFactories();
    //}


	//public void setSessionFactoryContainer(AdapterSessionFactoryContainer sessionFactoryContainer) {
      //  this.sessionFactoryContainer = sessionFactoryContainer;
    //}


    /** Gets connection to the database **/
    public  Connection getConnection() {
    	//Session session = sessionFactory.getCurrentSession();
    	Connection connection = null;
    	try {
    	   connection = getDataSource().getConnection();
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
       return connection;
    }


    /** Gets connection to the database indicated via the session factory key **/
    public synchronized Connection getConnection(String schemaId) {
		Connection connection = null;
		try {
			connection = getDataSourceMap().get(schemaId).getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return connection;
    }


    /** Releases connection to the database **/
    public void releaseConnection(Connection conn) {
        try {
          conn.close();
          //logger.debug("releasing connection from adapter...");
        }
        catch (Exception e) {
          //logger.error("Error while closing the connection.");
          e.printStackTrace();
        }
    }


	public ComboPooledDataSource getDataSource() {
		return dataSource;
	}


	@Autowired
	public void setDataSource(ComboPooledDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Map<String,ComboPooledDataSource> getDataSourceMap() {
		if(dataSourceMap==null)
			dataSourceMap = (Map<String,ComboPooledDataSource>)SpringContext.getApplicationContext().getBean("dataSourceMap");

		return dataSourceMap;
	}

	public void setdataSourceMap(Map<String,ComboPooledDataSource> dataSourceMap) {
		this.dataSourceMap = dataSourceMap;
	}

}
