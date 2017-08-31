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
package org.onap.portalsdk.workflow.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.workflow.models.Workflow;
import org.onap.portalsdk.workflow.models.WorkflowLite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
@Repository
public class WorkflowDAOImpl implements WorkflowDAO{
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public Workflow save(Workflow workflow, String creatorId){
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        
        try{
        	Query query = session.createQuery("from User where loginId =:loginId");
        	query.setParameter("loginId", creatorId);
        	User creator = (User)(query.list().get(0));
        	
        	workflow.setCreatedBy(creator);
            workflow.setCreated(new Date());
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        long id = (Long) session.save(workflow);
        Workflow savedWorkflow = (Workflow) session.get(Workflow.class, id);        
        tx.commit();
        session.close();
        return savedWorkflow;
	}
	
	public List<Workflow> getWorkflows(){
		Session session = this.sessionFactory.openSession();
        @SuppressWarnings("unchecked")
		List<Workflow> workflows = session.createQuery("from Workflow").list();
        session.close();
        return workflows;
	}

	@Override
	public void delete(Long workflowId) {
		Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("delete from Workflow where id =:id");
        query.setParameter("id", workflowId);
        query.executeUpdate();    
        tx.commit();
        session.close();        	
    }

	@Override
	public Workflow edit(WorkflowLite workflowLight, String creatorId) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        
        Query query = session.createQuery("from User where loginId =:loginId");
    	query.setParameter("loginId", creatorId);
    	User creator = (User)(query.list().get(0));
    	
    	Workflow workflowToModify = (Workflow) session.get(Workflow.class, workflowLight.getId());
        
    	workflowToModify.setActive(workflowLight.getActive().equalsIgnoreCase("true") ? true : false );
    	workflowToModify.setSuspendLink(workflowLight.getSuspendLink());
    	workflowToModify.setRunLink(workflowLight.getRunLink());
    	workflowToModify.setDescription(workflowLight.getDescription());
    	workflowToModify.setWorkflowKey(workflowLight.getWorkflowKey());
    	workflowToModify.setName(workflowLight.getName());
    	
    	workflowToModify.setModifiedBy(creator);
    	workflowToModify.setLastUpdated(new Date());
        
        session.update(workflowToModify);
        Workflow savedWorkflow = (Workflow) session.get(Workflow.class, workflowLight.getId());
        tx.commit();
        session.close();
        return savedWorkflow;
	}
}
