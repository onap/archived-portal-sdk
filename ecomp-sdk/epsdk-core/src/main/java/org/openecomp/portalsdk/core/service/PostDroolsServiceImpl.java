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
package org.openecomp.portalsdk.core.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.openecomp.portalsdk.core.command.PostDroolsBean;
import org.openecomp.portalsdk.core.drools.DroolsRuleService;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO REFACTOR
 *
 */
@Service("postDroolsService")
@Transactional
public class PostDroolsServiceImpl implements PostDroolsService{
	
	static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PostDroolsServiceImpl.class);

	@Override
	public String execute(String droolsFile, String className, String selectedRules) {
		logger.info(EELFLoggerDelegate.applicationLogger, "Executing Drools...");
		String resultsString = executeDemoRules(droolsFile, className, selectedRules);
		return resultsString;
	}

	
	public List<PostDroolsBean> fetchDroolBeans() {
		
		List<PostDroolsBean> beanList = new ArrayList<PostDroolsBean>();
		Path path = FileSystems.getDefault().getPath(SystemProperties.getProperty(SystemProperties.FILES_PATH));
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path,"*.{drl}")) {
	           for (Path entry: stream) {
	        	   
	        	PostDroolsBean postDroolsBean = new PostDroolsBean();
				String fileName = entry.getName(entry.getNameCount()-1).toString();
				postDroolsBean.setDroolsFile(fileName);//sample populated
				postDroolsBean.setClassName(retrieveClass(fileName));
				//postDroolsBean.setSelectedRules("[\"NJ\",\"NY\",\"KY\"]");
				beanList.add(postDroolsBean);
	           }
	       } catch (DirectoryIteratorException ex) {
	           logger.error(EELFLoggerDelegate.errorLogger, ex.getMessage());
	       } catch (IOException e) {
			   logger.error(EELFLoggerDelegate.errorLogger, e.getMessage());
		}
	       return beanList;
	}
	
	@Override
	public String retrieveClass(String fileName) {
		String resultsString = "";
		try {
			// load up the knowledge base
			final KnowledgeBuilder kbuilder = loadKBuilder(fileName);
			final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
			return pkgs.iterator().next().getFactTypes().iterator().next().getFactClass().getName();
			
		} catch (Throwable t) {
			logger.error(EELFLoggerDelegate.errorLogger, t.getMessage());
		}
		
		return resultsString;
	}
	
	protected static KnowledgeBuilder loadKBuilder(String fileName) {
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

		// this will parse and compile in one step
		kbuilder.add(ResourceFactory.newFileResource(SystemProperties.getProperty(SystemProperties.FILES_PATH) + File.separator + fileName),
		//kbuilder.add(ResourceFactory.newClassPathResource(SystemProperties.getProperty(SystemProperties.FILES_PATH) + File.separator + drl_file_path, DroolsRuleService.class),
				ResourceType.DRL);
		// kbuilder.add(ResourceFactory.newClassPathResource("rules.drl",DroolsRuleService.class),
		// ResourceType.DRL);

		// Check the builder for errors
		if (kbuilder.hasErrors()) {

			logger.error(EELFLoggerDelegate.errorLogger, kbuilder.getErrors().toString());

			throw new RuntimeException("Unable to compile \".drl\".");

		}
		return kbuilder;
	}
	

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static String executeDemoRules(String fileName, String className, String ruleValue) {
		String resultsString = "";
		try {
			// load up the knowledge base
			// KieServices ks = KieServices.Factory.get();
			// KieContainer kContainer = ks.getKieClasspathContainer();
			// KieSession kSession = kContainer.newKieSession("ksession-rules");

			final KnowledgeBuilder kbuilder = loadKBuilder(fileName);

			// get the compiled packages (which are serializable)

			final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
			
			// add the packages to a knowledgebase (deploy the knowledge
			// packages).

			final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

			kbase.addKnowledgePackages(pkgs);

			final StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession();

			ObjectMapper mapper = new ObjectMapper();
			if(ruleValue == null || ruleValue.equals("")) {
				resultsString = "Please enter valid rule";
				return resultsString;
			}
			List<String> selectedRules = mapper.readValue(ruleValue, List.class);
			List<String> ruleResponse = new ArrayList<String>();
			
			for(String rule : selectedRules){
				Class<DroolsRuleService> clazz = (Class<DroolsRuleService>) Class.forName(className);
				DroolsRuleService droolsIntroduction =clazz.newInstance();
				droolsIntroduction.init(rule);
				kSession.insert(droolsIntroduction);
				kSession.fireAllRules();
				ruleResponse.add(droolsIntroduction.getResultsString());
			}
			
			resultsString = mapper.writeValueAsString(ruleResponse);
			
//			kSession.insert(new DroolsRuleService("KY"));
//			kSession.fireAllRules();
//
//			kSession.setGlobal("age", "25");
//			kSession.insert(new DroolsRuleService("NY"));
//			kSession.fireAllRules();
		} catch (Throwable t) {
			logger.error(EELFLoggerDelegate.errorLogger, t.getMessage());
		}
		
		return resultsString;
	}

	

}
