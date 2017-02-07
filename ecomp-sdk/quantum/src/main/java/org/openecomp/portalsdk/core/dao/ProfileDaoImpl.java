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
package org.openecomp.portalsdk.core.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openecomp.portalsdk.core.domain.Profile;
import org.springframework.stereotype.Repository;

@Repository("profileDao")
public class ProfileDaoImpl extends AbstractDao<Integer, Profile> implements ProfileDao{

	
	public List<Profile> findAll() {
		Criteria crit = getSession().createCriteria(Profile.class);
		@SuppressWarnings("unchecked")
		List<Profile> p = crit.list();
		
		return p;
	}

	
	public Profile getProfile(int id) {
		Criteria crit = getSession().createCriteria(Profile.class);
		crit.add(Restrictions.eq("id", id));
		Profile profile = (Profile) crit.uniqueResult();
		
		return profile;
	}

}
